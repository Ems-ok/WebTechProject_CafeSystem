export function renderLoginPage(mainAppDiv) {
    if (!mainAppDiv) return;

    mainAppDiv.innerHTML =`
        <div class="card">
            <h3 class="text-center mb-3">Mase Café Login</h3>
            <form id="loginForm">
                <div class="mb-2">
                    <input type="text" class="form-control" name="username" placeholder="Username" required>
                </div>
                <div class="mb-3">
                    <input type="password" class="form-control" name="password" placeholder="Password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Login</button>
            </form>
        </div>`

    const form = document.getElementById('loginForm');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = form.username.value;
        const password = form.password.value;

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password})
            });

            if (!response.ok) throw new Error('Invalid credentials');

            const data = await response.json();
            localStorage.setItem('token', data.token); // store JWT in local cache
            console.log(data.token);
            window.location.reload();
        } catch (err) {
            alert(err.message);
        }
    });
}