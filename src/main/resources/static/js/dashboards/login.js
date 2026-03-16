export function renderLoginPage(mainAppDiv) {
    if (!mainAppDiv) return;

    mainAppDiv.innerHTML = `
       <div class="login-page-wrapper">
            <div class="login-container">
                <h3 class="mb-4">Mase Café Login</h3>
        
                <form id="loginForm">
                    <div class="mb-3">
                        <input id="username" type="text" class="form-control" name="username" placeholder="Username" required>
                    </div>
        
                    <div class="mb-3">
                        <input id="password" type="password" class="form-control" name="password" placeholder="Password" required>
                    </div>
        
                    <button id="submit" type="submit" class="btn btn-primary w-100">Login</button>
                </form>
            </div>
        </div>`;

    $("#loginForm").submit(function (event) {

        event.preventDefault();

        const username = $("input[name='username']").val();
        const password = $("input[name='password']").val();

        $.ajax({
            url: "/auth/login",
            type: "POST",
            contentType: "application/json",

            data: JSON.stringify({
                username: username,
                password: password
            }),

            success: function (data) {

                localStorage.setItem("token", data.token);
                console.log(data.token);

                globalThis.location.reload();
            },

            error: function () {
                alert("Invalid credentials");
            }

        });

    });
}