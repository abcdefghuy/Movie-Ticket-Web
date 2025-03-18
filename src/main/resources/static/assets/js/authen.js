async function refreshAccessToken() {
    try {
        let response = await fetch('/refresh', {
            method: 'POST',
            credentials: 'include' // Đảm bảo gửi cookie refreshToken
        });

        if (response.ok) {
            console.log(" Refresh token thành công! Tiếp tục phiên làm việc...");
        } else {
            console.log(" Refresh token thất bại. Chuyển hướng về trang đăng nhập...");
            window.location.href = "/login";
        }
    } catch (error) {
        console.error("Lỗi khi refresh token:", error);
        window.location.href = "/login";
    }
}

// 🌟 Cấu hình refresh token tự động mỗi 10 phút
setInterval(refreshAccessToken, 10 * 60 * 1000);