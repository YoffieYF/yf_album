import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
// import 'package:album/album.dart';

void main() {
  const MethodChannel channel = MethodChannel('album');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
//    expect(await Album.platformVersion, '42');
  });
}
