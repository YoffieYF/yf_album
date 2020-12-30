#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint album.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'album'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter plugin.'
  s.description      = <<-DESC
A new Flutter plugin.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.vendored_frameworks       = 'Framework/*.framework'
  s.resource_bundles = {
    'WFAlbum' => ['Assets/*']
  }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'SDWebImage', '5.0.6'
  s.dependency 'Masonry', '1.1.0'
  s.dependency 'YYImage/WebP', '1.0.4'
  s.dependency 'YYCache', '1.0.4'
  s.dependency 'SVProgressHUD', '2.2.5'
  s.dependency 'QTEventBus', '0.4.1'
  s.platform = :ios, '8.0'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
