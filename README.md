# Jollibug - Hệ Thống Đặt Đồ Ăn Nhanh Trực Tuyến
Jollibug là đồ án xây dựng hệ thống bán thức ăn nhanh trực tuyến, hỗ trợ khách hàng đặt món, quản lý đơn hàng và vận hành cửa hàng theo nhiều vai trò. Dự án được phát triển theo kiến trúc web application với `Spring Boot`, `Spring MVC`, `JSP/Servlet`, `JPA/Hibernate` và cơ sở dữ liệu `Oracle`.

## Mục lục
1. [Giới thiệu đồ án](#giới-thiệu-đồ-án)
2. [Thành viên nhóm](#thành-viên-nhóm)
3. [Điểm nổi bật](#điểm-nổi-bật)
4. [Chức năng chính](#chức-năng-chính)
5. [Công nghệ sử dụng](#công-nghệ-sử-dụng)
6. [Cấu trúc dự án](#cấu-trúc-dự-án)
7. [Yêu cầu môi trường](#yêu-cầu-môi-trường)
8. [Hướng dẫn cài đặt và chạy dự án](#hướng-dẫn-cài-đặt-và-chạy-dự-án)
9. [Cấu hình hệ thống](#cấu-hình-hệ-thống)
10. [Dữ liệu mẫu](#dữ-liệu-mẫu)
11. [Hình ảnh minh họa](#hình-ảnh-minh-họa)
12. [Hướng phát triển](#hướng-phát-triển)
13. [Ghi chú](#ghi-chú)

## Giới thiệu đồ án
- Tên đề tài: Hệ thống quản lý đặt thức ăn nhanh online 
- Mục tiêu dự án: 
- Bài toán thực tế cần giải quyết: 
- Đối tượng sử dụng:
- Thời gian thực hiện: 1/2/2026 - 25/5/2026

### Mô tả ngắn
> Jollibug là hệ thống hỗ trợ kinh doanh thức ăn nhanh, cho phép khách hàng xem thực đơn, đặt món, theo dõi đơn hàng và đánh giá sản phẩm. Đồng thời, hệ thống cũng cung cấp các trang quản trị dành cho nhân viên, quản lý và quản trị viên để xử lý đơn, theo dõi doanh thu, quản lý tài khoản, sản phẩm, danh mục, mã giảm giá và chương trình khuyến mãi.

## Thành viên nhóm
| STT | Họ và tên | MSSV | Vai trò | Phân công |
|---|---|---|---|---|
| 1 | Nguyễn Mạnh Trí | 24521834 | Nhóm trưởng | Phân tích yêu cầu , Module User , CSDL
| 2 | Huỳnh Nguyễn Hoàng Thịnh | 2452xxxx | Thành viên | Giao diện, JSP , Module Admin , CSKH 
| 3 | Nguyễn Bá Thiên | 2452 | Thành viên |  kiểm thử  , Thống kê , Giảm giá  , Đánh giá
| 4 | Nguyễn Khánh Vi | 2452xxxx | Thành viên | CSDL ,  đặt hàng , thanh toán , giỏ hàng
| 4 | Trần Thư | 2452xxxx | Thành viên , module Quản lý , module Staff (nhân viên)

## Điểm nổi bật

- Xây dựng hệ thống đặt đồ ăn theo mô hình nhiều vai trò: `Client`, `Staff`, `Manager`, `Admin`.
- Hỗ trợ đầy đủ quy trình từ xem thực đơn, thêm giỏ hàng, thanh toán, tạo đơn đến theo dõi trạng thái đơn.
- Có các module quản trị cho sản phẩm, danh mục, voucher, khuyến mãi và thống kê kinh doanh.
- Tích hợp chat hỗ trợ khách hàng theo thời gian thực với `WebSocket`.
- Có luồng xác thực tài khoản và hỗ trợ quên mật khẩu qua email.
- Tổ chức dự án theo cấu trúc rõ ràng với `Controller`, `Service`, `Repository`, `Entity`, `DTO`, `DAO`.

## Chức năng chính
### 1. Khách hàng
- Đăng ký, đăng nhập, đăng xuất.
- Xác thực email và khôi phục mật khẩu.
- Xem trang chủ, giới thiệu và thực đơn.
- Xem chi tiết món ăn.
- Thêm sản phẩm vào giỏ hàng.
- Quản lý giỏ hàng và cập nhật số lượng sản phẩm.
- Quản lý địa chỉ giao hàng.
- Áp dụng voucher giảm giá.
- Thanh toán và tạo đơn hàng.
- Theo dõi lịch sử đơn hàng.
- Xem chi tiết đơn hàng và trạng thái xử lý.
- Hủy đơn, xác nhận đã nhận hàng, đặt lại đơn cũ.
- Đánh giá đơn hàng và xem phản hồi đánh giá.
- Cập nhật hồ sơ cá nhân.
- Chat hỗ trợ với cửa hàng.

### 2. Nhân viên
- Xem danh sách đơn hàng.
- Xem chi tiết đơn hàng.
- Xác nhận đơn và cập nhật trạng thái đơn.
- Theo dõi danh sách khách hàng.
- Trả lời đánh giá/hỗ trợ của khách hàng.
- Hỗ trợ chat với khách hàng.

### 3. Quản lý
- Xem dashboard quản lý hoạt động kinh doanh.
- Quản lý sản phẩm.
- Quản lý danh mục món ăn.
- Quản lý mã giảm giá.
- Quản lý chương trình khuyến mãi.
- Theo dõi thống kê doanh thu, đơn hàng, khách hàng.

### 4. Quản trị viên
- Quản lý người dùng trong hệ thống.
- Tạo tài khoản mới.
- Cập nhật thông tin tài khoản.
- Khóa, mở khóa và xóa tài khoản.
- Theo dõi tổng quan trạng thái người dùng.

## Công nghệ sử dụng

### Backend

- `Java 17`
- `Spring Boot`
- `Spring MVC`
- `REST API` cho giỏ hàng (`/api/cart/items`) kết hợp `AJAX/fetch`
- `Spring Data JPA`
- `Hibernate`
- `Spring Validation`
- `Spring Mail`
- `Spring WebSocket`
- `Spring Session JDBC`

### Frontend

- `JSP`
- `JSTL`
- `HTML5`
- `CSS3`
- `JavaScript`
- `Chart.js`

### Database

- `Oracle Database`
- Có khai báo thêm `H2` trong dependency để phục vụ môi trường runtime/phát triển nếu cần

### Build Tool

- `Maven`

## Cấu trúc dự án

```text
Jollibug/
|-- src/
|   |-- main/
|   |   |-- java/vn/fastfood/
|   |   |   |-- config/        # Cấu hình ứng dụng, MVC, WebSocket, session
|   |   |   |-- controller/    # Controller cho client, staff, manager, admin
|   |   |   |-- dao/           # DAO cho một số nghiệp vụ riêng
|   |   |   |-- dto/           # DTO trao đổi dữ liệu
|   |   |   |-- entity/        # Entity ánh xạ database
|   |   |   |-- model/         # Model nghiệp vụ bổ sung
|   |   |   |-- repository/    # JPA Repository
|   |   |   |-- service/       # Business logic
|   |   |   `-- util/          # Tiện ích dùng chung
|   |   |-- resources/
|   |   |   |-- application.properties
|   |   |   `-- data.sql
|   |   `-- webapp/
|   |       |-- WEB-INF/view/  # Giao diện JSP theo từng vai trò
|   |       `-- resources/     # CSS, JS, images
|   `-- test/
|       `-- java/
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
`-- sampledb.sql
```

## Cart API và AJAX

Luồng thêm vào giỏ hàng phía client đã dùng `REST API` thay cho submit form đồng bộ:

- Các form có `data-ajax-add-cart` sẽ được file `src/main/webapp/resources/js/client/add-to-cart.js` bắt sự kiện.
- Client gửi `POST /api/cart/items` bằng `fetch` với các field như `productID` và `quantity`.
- Server trả JSON gồm `success`, `message`, `cartCount`.
- Giao diện cập nhật ngay số lượng trên header mà không cần reload trang.

API hiện có:

```http
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items
DELETE /api/cart/items?maMon={id}
```

## Yêu cầu môi trường

- `JDK 17`
- `Maven 3.9+` hoặc dùng `Maven Wrapper` có sẵn
- `Oracle Database`
- IDE khuyến nghị: `IntelliJ IDEA`, `Eclipse`, `VS Code`

## Hướng dẫn cài đặt và chạy dự án

### 1. Clone dự án

```bash
git clone <repository-url>
cd Jollibug
```

### 2. Tạo cơ sở dữ liệu

Chuẩn bị một database/schema Oracle cho dự án.

Ví dụ cấu hình hiện tại đang kết nối tới:

```properties
jdbc:oracle:thin:@localhost:1521/mynewprojectdb
```

Bạn có thể:

- Tạo database/schema tương ứng trong Oracle.
- Hoặc chỉnh lại `src/main/resources/application.properties` theo môi trường máy của bạn.

### 3. Cấu hình tài khoản database và email

Mở file [application.properties](D:/Storage/Documents/Jollibug/src/main/resources/application.properties) và cập nhật:

```properties
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

spring.mail.host=...
spring.mail.port=...
spring.mail.username=...
spring.mail.password=...
```

### 4. Chạy ứng dụng

Nếu dùng Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Trên Windows:

```bash
mvnw.cmd spring-boot:run
```

Hoặc dùng Maven đã cài sẵn:

```bash
mvn spring-boot:run
```

### 5. Truy cập hệ thống

Sau khi chạy thành công, truy cập:

```text
http://localhost:8080/
```

## Cấu hình hệ thống

Một số điểm đáng chú ý trong cấu hình hiện tại:

- Ứng dụng dùng `OracleDialect` cho Hibernate.
- `spring.jpa.hibernate.ddl-auto=create` đang bật, nghĩa là khi chạy có thể tạo lại cấu trúc dữ liệu.
- `spring.sql.init.mode=always` và `data.sql` sẽ được nạp tự động.
- Dự án có cấu hình gửi email để xác thực tài khoản/quên mật khẩu.
- Dự án có hỗ trợ WebSocket cho tính năng chat.

### Lưu ý bảo mật

- Không nên giữ trực tiếp tài khoản database hoặc API key email trong file `application.properties` khi đưa lên GitHub công khai.
- Nên chuyển các thông tin nhạy cảm sang biến môi trường hoặc file cấu hình riêng cho từng máy.
- Trong mã nguồn hiện tại, lớp `FastFoodApplication` đang loại trừ một số cấu hình auto security để thuận tiện cho quá trình phát triển. Khi triển khai thực tế, nên rà soát và bật lại cơ chế bảo mật phù hợp.

## Dữ liệu mẫu

Dự án hiện có các nguồn dữ liệu hỗ trợ khởi tạo:

- [data.sql](D:/Storage/Documents/Jollibug/src/main/resources/data.sql)
- [sampledb.sql](D:/Storage/Documents/Jollibug/sampledb.sql)

Nếu gặp lỗi khi khởi tạo dữ liệu:

- Kiểm tra version Oracle đang dùng.
- Kiểm tra encoding của file SQL.
- Kiểm tra tên bảng/cột có đồng bộ với entity hiện tại hay không.

## Hình ảnh minh họa

- Ảnh giao diện trang chủ
```md
![Trang chủ](src/main/webapp/resources/images/homepage.png)
```
- Ảnh trang thực đơn
```md
![Trang chủ](src/main/webapp/resources/images/menu.png)
```
- Ảnh giỏ hàng 
```md
![Trang chủ](src/main/webapp/resources/images/cart.png)
```
- Ảnh đặt hàng
```md
![Trang chủ](src/main/webapp/resources/images/checkout.png)
```
Ảnh thanh toán
```md
![Trang chủ](src/main/webapp/resources/images/payment.png)
```
- Ảnh dashboard quản lý
```md
![Trang chủ](src/main/webapp/resources/images/manager.png)
```
- Ảnh dashboard admin
```md
![Trang chủ](src/main/webapp/resources/images/admin.png)
```

- Ảnh dashboard staff
```md
![Trang chủ](src/main/webapp/resources/images/staff.png)
```

## Hướng phát triển

- Tích hợp thanh toán trực tuyến hoàn chỉnh hơn.
- Hoàn thiện phân quyền bảo mật cho từng vai trò.
- Bổ sung thống kê nâng cao và báo cáo xuất file.
- Tối ưu giao diện trên thiết bị di động.
- Tách cấu hình môi trường `dev`, `test`, `prod`.
- Bổ sung test cho service, controller và repository.
- Triển khai CI/CD và logging tập trung.

## Ghi chú

- Dự án đang phù hợp để trình bày như một đồ án môn học hoặc đồ án nhóm phát triển web Java.
- README này đã chừa sẵn các phần để nhóm tùy chỉnh lại theo đúng thông tin chính thức trước khi nộp.

<!-- có thể bổ sung thêm:
  - sơ đồ use case
  - sơ đồ cơ sở dữ liệu
  - sơ đồ kiến trúc hệ thống
  - video demo
  - bảng phân công công việc và tiến độ -->
