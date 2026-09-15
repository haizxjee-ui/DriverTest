# DriverTest

Ứng dụng Android hỗ trợ ôn tập và thi thử lý thuyết bằng lái xe ô tô.

## 📱 Giới thiệu

**DriverTest** là ứng dụng Android được xây dựng nhằm hỗ trợ người học ôn luyện kiến thức lý thuyết và thực hiện các bài thi thử bằng lái xe ô tô.

Ứng dụng cung cấp hệ thống câu hỏi, đề thi ngẫu nhiên, chấm điểm và hỗ trợ người dùng xem lại những câu hỏi đã làm sai.

## 🎯 Mục tiêu

* Hỗ trợ ôn tập kiến thức lý thuyết lái xe ô tô.
* Hỗ trợ ôn tập theo từng chủ đề.
* Hỗ trợ ôn tập toàn bộ bộ câu hỏi.
* Tạo đề thi ngẫu nhiên theo hạng bằng.
* Mô phỏng bài thi lý thuyết.
* Chấm điểm và hiển thị kết quả.
* Lưu lịch sử làm bài.
* Hỗ trợ xem và ôn tập lại các câu hỏi đã làm sai.

## ✨ Chức năng chính

* Xem lịch sử làm bài thi
* Tạo đề thi ngẫu nhiên
* Làm bài thi
* Nộp bài thi
* Xem kết quả bài thi
* Xem chi tiết bài thi
* Xem các câu hỏi làm sai
* Ôn tập các câu hỏi làm sai
* Chọn hạng bằng
* Thay đổi hạng bằng
* Ôn tập theo chủ đề
* Ôn tập bộ 600 câu hỏi
* Xem kết quả ôn tập

## 🛠 Công nghệ sử dụng

* **Kotlin** – Ngôn ngữ lập trình
* **XML** – Xây dựng giao diện
* **MVVM** – Kiến trúc ứng dụng
* **Hilt** – Dependency Injection
* **Room** – Cơ sở dữ liệu cục bộ
* **Retrofit** – Gọi REST API
* **Gson** – Phân tích dữ liệu JSON
* **Coroutines** – Xử lý bất đồng bộ
* **Flow** – Quan sát và xử lý dữ liệu
* **Navigation Component** – Điều hướng giữa các màn hình
* **DataStore** – Lưu trữ thiết lập người dùng
* **Coil** – Tải và hiển thị hình ảnh
* **JUnit** – Unit Test
* **MockK** – Mock trong Unit Test
* **Espresso** – Kiểm thử giao diện

## 🏗 Kiến trúc

Ứng dụng sử dụng kiến trúc **MVVM (Model - View - ViewModel)** kết hợp với Repository Pattern.

```text
┌─────────────────────┐
│         View        │
│   Activity/Fragment │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│      ViewModel      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     Repository      │
└──────────┬──────────┘
           │
      ┌────┴────┐
      ▼         ▼
┌──────────┐ ┌──────────┐
│   Room   │ │ Retrofit │
│ Database │ │   API    │
└──────────┘ └──────────┘

## 🌐 API

Ứng dụng sử dụng REST API để lấy dữ liệu câu hỏi và loại câu hỏi.
Dữ liệu API được chuyển đổi sang các model của ứng dụng và lưu trữ vào Room Database để sử dụng trong ứng dụng.

## 🚗 Hạng bằng

Ứng dụng hỗ trợ các hạng bằng lái xe ô tô:

* B
* C1
* C
* D
* BE
* D1
* D2
* C1E
* CE
* D1E
* D2E
* DE

## ⚙️ Yêu cầu môi trường
* Android Studio
* Kotlin
* Gradle
* Android SDK
* Minimum SDK: **29**
* Compile SDK: **37**

Project được thực hiện với mục đích học tập và xây dựng đồ án ứng dụng Android.
