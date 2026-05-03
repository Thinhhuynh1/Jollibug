<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đơn hàng</title>
  <meta name="description" content="Thông tin đơn hàng của tôi" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout ">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Chỉnh sửa đánh giá đơn hàng #DH001</h1>

          <div class="review-container">
              <input type="hidden" name="orderId" value="DH001" />
              <input type="hidden" name="reviewId" value="REV001" />

              <div class="form-group">
                <label class="form-label">Chất lượng sản phẩm</label>
                <div class="star-rating">
                  <input type="radio" id="star5" name="rating" value="5" required />
                  <label for="star5" title="5 sao">★</label>
                  <input type="radio" id="star4" name="rating" value="4" />
                  <label for="star4" title="4 sao">★</label>
                  <input type="radio" id="star3" name="rating" value="3" />
                  <label for="star3" title="3 sao">★</label>
                  <input type="radio" id="star2" name="rating" value="2" />
                  <label for="star2" title="2 sao">★</label>
                  <input type="radio" id="star1" name="rating" value="1" />
                  <label for="star1" title="1 sao">★</label>
                </div>
              </div>

              <div class="form-group">
                <label for="reviewContent" class="form-label">Nhận xét của bạn</label>
                <textarea class="form-control" id="reviewContent" name="content" rows="5" placeholder="Hãy chia sẻ những điều bạn thích về món ăn này nhé..." style="resize: none; overflow: hidden;" oninput="this.style.height = 'auto'; this.style.height = this.scrollHeight + 'px'" required></textarea>
              </div>

              <div class="form-group">
                <label for="reviewImages" class="form-label">Thêm hình ảnh</label>
                <div class="file-upload-wrapper">
                  <input type="file" class="form-control-file" id="reviewImages" name="images" accept="image/*" multiple />
                  <small class="form-text">Bạn có thể chọn nhiều ảnh để tải lên (tối đa 5 ảnh).</small>
                </div>
              </div>

              <div class="form-actions">
                <a href="/orders/delivered" class="btn btn-secondary">Hủy bỏ</a>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
              </div>
          </div>
          
          </section>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
