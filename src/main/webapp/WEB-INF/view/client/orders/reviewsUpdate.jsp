<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Sửa đánh giá</title>
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
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Sửa đánh giá — ${review.monAn.tenMon}</h1>

          <div class="client-flash" style="background:#f8fafc;border:1px solid #e2e8f0;color:#334155;margin-bottom:1rem;">
            Ngày đánh giá lần đầu: <strong>${review.ngayDGDisplay}</strong>.
            Hạn sửa cuối: <strong>${editDeadlineDisplay}</strong>.
          </div>

          <div class="review-container">
            <form action="/orders/reviews/update" method="post">
              <input type="hidden" name="reviewId" value="${review.maDG}" />

              <div class="form-group">
                <label class="form-label">Chất lượng sản phẩm</label>
                <div class="star-rating">
                  <input type="radio" id="star5" name="sao" value="5" ${review.sao == 5 ? 'checked' : ''} required />
                  <label for="star5" title="5 sao">★</label>
                  <input type="radio" id="star4" name="sao" value="4" ${review.sao == 4 ? 'checked' : ''} />
                  <label for="star4" title="4 sao">★</label>
                  <input type="radio" id="star3" name="sao" value="3" ${review.sao == 3 ? 'checked' : ''} />
                  <label for="star3" title="3 sao">★</label>
                  <input type="radio" id="star2" name="sao" value="2" ${review.sao == 2 ? 'checked' : ''} />
                  <label for="star2" title="2 sao">★</label>
                  <input type="radio" id="star1" name="sao" value="1" ${review.sao == 1 ? 'checked' : ''} />
                  <label for="star1" title="1 sao">★</label>
                </div>
              </div>

              <div class="form-group">
                <label for="reviewContent" class="form-label">Nhận xét của bạn</label>
                <textarea class="form-control" id="reviewContent" name="noiDung" rows="5" required>${review.noiDung}</textarea>
              </div>

              <div class="form-actions">
                <a href="/orders/reviews/view?reviewId=${review.maDG}" class="btn btn-secondary">Hủy</a>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
              </div>
            </form>
          </div>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
