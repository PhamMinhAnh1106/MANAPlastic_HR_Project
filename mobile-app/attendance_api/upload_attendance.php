<?php
header('Content-Type: application/json');

// Cấu hình kết nối database
$dbHost = 'localhost';
$dbUser = 'root';      
$dbPass = '';        
$dbName = 'attendace_system'; 

$conn = new mysqli($dbHost, $dbUser, $dbPass, $dbName);

if ($conn->connect_error) {
    echo json_encode(['success' => false, 'message' => 'Lỗi kết nối CSDL: ' . $conn->connect_error]);
    exit();
}

// Hàm trả về phản hồi và thoát
function send_response($success, $message) {
    global $conn;
    echo json_encode(['success' => $success, 'message' => $message]);
    $conn->close();
    exit();
}

// Kiểm tra phương thức request phải là POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    send_response(false, 'Phương thức không hợp lệ.');
}

// Lấy dữ liệu từ request
$employeeId = isset($_POST['employee_id']) ? $_POST['employee_id'] : '';
$imageData = isset($_POST['image_data']) ? $_POST['image_data'] : '';

if (empty($employeeId) || empty($imageData)) {
    send_response(false, 'Thiếu dữ liệu mã nhân viên hoặc hình ảnh.');
}

// Giải mã chuỗi base64 thành dữ liệu ảnh
$decodedImage = base64_decode($imageData);
if ($decodedImage === false) {
    send_response(false, 'Dữ liệu ảnh không hợp lệ.');
}

// Tạo thư mục 'uploads' nếu nó chưa tồn tại
$uploadDir = 'uploads/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

// Tạo tên file duy nhất
$imageName = uniqid() . '_' . time() . '.jpg';
$imagePath = $uploadDir . $imageName;

// Lưu file ảnh vào server
if (file_put_contents($imagePath, $decodedImage)) {
    // Lưu đường dẫn tương đối vào database
    $imageUrl = $imagePath;

    // Chuẩn bị câu lệnh SQL để tránh SQL Injection
    $stmt = $conn->prepare("INSERT INTO attendances (employee_id, check_in_image_url) VALUES (?, ?)");
    if ($stmt === false) {
        send_response(false, 'Lỗi chuẩn bị câu lệnh SQL: ' . $conn->error);
    }

    $stmt->bind_param("ss", $employeeId, $imageUrl);

    if ($stmt->execute()) {
        send_response(true, 'Chấm công thành công!');
    } else {
        send_response(false, 'Lỗi khi lưu vào CSDL: ' . $stmt->error);
    }
    $stmt->close();
} else {
    send_response(false, 'Lỗi khi lưu file ảnh.');
}

$conn->close();
?>
