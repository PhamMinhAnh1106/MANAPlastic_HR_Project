import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:mobile_scanner/mobile_scanner.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Chấm Công MANAPlastic',
      theme: ThemeData(
        primarySwatch: Colors.teal,
        visualDensity: VisualDensity.adaptivePlatformDensity,
        scaffoldBackgroundColor: Colors.grey[50],
      ),
      debugShowCheckedModeBanner: false,
      // mở app là quét luôn ( màn hình quét QR )
      home: const QRScannerScreen(),
    );
  }
}

class QRScannerScreen extends StatefulWidget {
  const QRScannerScreen({super.key});

  @override
  State<QRScannerScreen> createState() => _QRScannerScreenState();
}

class _QRScannerScreenState extends State<QRScannerScreen> {
  bool _isProcessing = false;
  bool _isScanCooldown = false;

  final MobileScannerController _scannerController = MobileScannerController(
    facing: CameraFacing.front,
    torchEnabled: false,
    returnImage: true,
  );

  // Hàm upload dữ liệu chấm công
  Future<void> _uploadAttendance(
    String employeeId,
    Uint8List imageBytes,
  ) async {
    setState(() {
      _isProcessing = true;
    });

    const String serverUrl =
        'http://192.168.1.57/attendance_api/upload_attendance.php';

    try {
      String base64Image = base64Encode(imageBytes);
      final response = await http
          .post(
            Uri.parse(serverUrl),
            body: {'employee_id': employeeId, 'image_data': base64Image},
          )
          .timeout(const Duration(seconds: 20));

      if (response.statusCode == 200) {
        final result = jsonDecode(response.body);
        if (result['success']) {
          // Hiển thị thanh bar thông báo
          _showSuccessSnackbar('Đã chấm công cho mã: $employeeId');
          //gọi hàm thời gian đếm ngược của alert
          _startCooldown();
        } else {
          _showErrorDialog('Lỗi Server: ${result['message']}');
        }
      } else {
        _showErrorDialog('Lỗi kết nối đến server: ${response.statusCode}');
      }
    } catch (e) {
      _showErrorDialog(
        'Không thể kết nối đến server. Vui lòng kiểm tra lại kết nối mạng.\nChi tiết: $e',
      );
    } finally {
      // Dù thành công hay thất bại thì cũng kết thúc trạng thái xử lý
      if (mounted && !_isScanCooldown) {
        setState(() {
          _isProcessing = false;
        });
      }
    }
  }

  // Hàm hiển thị thanh Bar thành công
  void _showSuccessSnackbar(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          message,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: Colors.green.shade600,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        margin: const EdgeInsets.all(16),
        duration: const Duration(seconds: 3),
      ),
    );
  }

  //  Hàm bắt đầu cooldown đếm ngược
  void _startCooldown() {
    if (!mounted) return;
    setState(() {
      _isScanCooldown = true;
      _isProcessing = false; // Kết thúc quá trình xử lý để giao diện trở lại bình thường
    });
    // thời gian sống của cái thanh bar - sau 2 giây
    Timer(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _isScanCooldown = false;
        });
      }
    });
  }

  // Hàm báo lỗi chỉ cần reset trạng thái, không cần start lại cam
  void _showErrorDialog(String message) {
    if (!mounted) return;
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Chấm công thất bại'),
        content: Text(message),
        actions: [
          TextButton(
            child: const Text('OK'),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Quét Mã Chấm Công')),
      body: Stack(
        fit: StackFit.expand,
        children: [
          MobileScanner(
            controller: _scannerController,
            onDetect: (capture) {
              // Nếu đang xử lý hoặc đang trong cooldown thì bỏ qua
              if (_isProcessing ||
                  _isScanCooldown ||
                  capture.barcodes.isEmpty) {
                return;
              }
              final String? employeeId = capture.barcodes.first.rawValue;
              final Uint8List? imageBytes = capture.image;
              if (employeeId != null &&
                  employeeId.isNotEmpty &&
                  imageBytes != null) {
                _uploadAttendance(employeeId, imageBytes);
              }
            },
          ),
          // Lớp phủ và khung ngắm (ô quét mã - khung quét)
          ColorFiltered(
            colorFilter: ColorFilter.mode(
              Colors.black.withOpacity(0.5),
              BlendMode.srcOut,
            ),
            child: Stack(
              fit: StackFit.expand,
              children: [
                Container(
                  decoration: const BoxDecoration(
                    color: Colors.black,
                    backgroundBlendMode: BlendMode.dstOut,
                  ),
                ),
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    height: 300,
                    width: 300,
                    decoration: BoxDecoration(
                      color: Colors.red,
                      borderRadius: BorderRadius.circular(20),
                    ),
                  ),
                ),
              ],
            ),
          ),
          Align(
            alignment: Alignment.center,
            child: Container(
              height: 300,
              width: 300,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: _isScanCooldown
                      ? Colors.amber.shade600
                      : Colors.white.withOpacity(0.8),
                  width: 4,
                ),
              ),
            ),
          ),
          // Hiển thị thông báo trạng thái
          Align(
            alignment: Alignment.bottomCenter,
            child: Container(
              margin: const EdgeInsets.only(bottom: 60),
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.7),
                borderRadius: BorderRadius.circular(10),
              ),
              child: _buildStatusWidget(),
            ),
          ),
        ],
      ),
    );
  }

  // Widget hiển thị trạng thái hiện tại (sẵn sàng, đang xử lý, cooldown)
  Widget _buildStatusWidget() {
    if (_isProcessing) {
      return const Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              color: Colors.white,
            ),
          ),
          SizedBox(width: 12),
          Text(
            'Đang xử lý...',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      );
    }
    if (_isScanCooldown) {
      return Text(
        'Sẵn sàng quét lại sau 2 giây...',
        style: TextStyle(
          color: Colors.amber.shade400,
          fontSize: 16,
          fontWeight: FontWeight.w600,
        ),
      );
    }
    return const Text(
      'Đưa mã QR vào trong khung',
      style: TextStyle(
        color: Colors.white,
        fontSize: 16,
        fontWeight: FontWeight.w600,
      ),
    );
  }

  @override
  void dispose() {
    _scannerController.dispose();
    super.dispose();
  }
}
