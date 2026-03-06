import {renderManagerDashboard} from "./managerDashboard.js";

export function renderDashboard(mainAppDiv) {
    mainAppDiv.innerHTML = `
        <nav class="navbar navbar-dark navbar-expand-lg top-navbar" aria-label="Top Navigation">
            <div class="container-fluid">
        
                <a class="navbar-brand d-flex align-items-center gap-2" href="#" id="brandLink">
                    <i class="bi bi-cup-hot-fill"></i>
                    Mase Café
                </a>
        
                    <div class="m-0">
                        <button id="logout_button" type="submit" class="btn btn-outline-light btn-sm">Logout</button>
                    </div>
                </div>
        
            </div>
        </nav>
        
        <div class="container-fluid">
            <div class="row">
                <nav class="col-md-2 sidebar" aria-label="Main Sidebar">
                    <ul class="nav flex-column">
                        <li class="nav-item role-restricted" data-allowed="MANAGER">
                            <a class="nav-link active" href="#" id="nav-dashboard">
                                <i class="bi bi-speedometer2 me-2"></i>
                                Dashboard
                            </a>
                        </li>
                    </ul>
                </nav>
        
                <main class="col-md-10 main-content">
                    <div id="dashboard-root"></div>
                </main>
            </div>
        </div>
        
        <div id="modal-container"></div>
        
        <script type="module" src="/js/app.js"></script>
        
        </body>
        </html>`;
    const loginButton = document.getElementById("logout_button");
    loginButton.addEventListener('click', () => {
       localStorage.setItem("token", null);
       window.location.reload();
    });
    const dashboardRoot = document.getElementById('dashboard-root');
    renderManagerDashboard(dashboardRoot)
}