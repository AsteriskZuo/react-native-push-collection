require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))
folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

rn_version_default = "0.72.17" # Change this to the version of React Native you are using
rn_version = rn_version_default
begin
  rn_package = JSON.parse(File.read(File.join(__dir__, "../..", "package.json")))
  rn_version = rn_package["dependencies"]["react-native"] || rn_version_default
rescue Errno::ENOENT
  puts "Error: File not found"
rescue JSON::ParserError
  puts "Error: Failed to parse JSON"
end

Pod::Spec.new do |s|
  s.name         = "react-native-push-collection"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/easemob/react-native-push-collection.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm}"

  s.dependency "GoogleUtilities"
  s.dependency "FirebaseCore", ">= 10.0.0"
  s.dependency "FirebaseMessaging", ">= 10.0.0"
  s.dependency "FirebaseAuth", ">= 10.0.0"

  s.xcconfig = {
    "OTHER_LDFLAGS": "-ObjC",
    'GCC_PREPROCESSOR_DEFINITIONS' => "RN_VERSION_MAJOR=#{rn_version.split('.')[0]} RN_VERSION_MINOR=#{rn_version.split('.')[1]} RN_VERSION_PATCH=#{rn_version.split('.')[2]}",
  }

  # Use install_modules_dependencies helper to install the dependencies if React Native version >=0.71.0.
  # See https://github.com/facebook/react-native/blob/febf6b7f33fdb4904669f99d795eba4c0f95d7bf/scripts/cocoapods/new_architecture.rb#L79.
  if respond_to?(:install_modules_dependencies, true)
    install_modules_dependencies(s)
  else
    s.dependency "React-Core"

    # Don't install the dependencies when we run `pod install` in the old architecture.
    if ENV['RCT_NEW_ARCH_ENABLED'] == '1' then
      s.compiler_flags = folly_compiler_flags + " -DRCT_NEW_ARCH_ENABLED=1"
      s.pod_target_xcconfig    = {
          "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/boost\"",
          "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
          "CLANG_CXX_LANGUAGE_STANDARD" => "c++17"
      }
      s.dependency "React-Codegen"
      s.dependency "RCT-Folly"
      s.dependency "RCTRequired"
      s.dependency "RCTTypeSafety"
      s.dependency "ReactCommon/turbomodule/core"
    end
  end
end
