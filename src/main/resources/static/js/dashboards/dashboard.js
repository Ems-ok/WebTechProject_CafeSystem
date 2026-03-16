import {renderManagerDashboard} from "./managerDashboard.js";
import {renderUserManagement} from "../features/userManagement.js";
import { renderMenuManagement } from "../features/renderMenuManagement.js"

export function renderDashboard(mainAppDiv) {

    mainAppDiv.innerHTML = `
        <nav class="navbar navbar-dark navbar-expand-lg top-navbar" aria-label="Top Navigation">
            <div class="container-fluid">
        
                <a class="navbar-brand d-flex align-items-center gap-2" href="#" id="brandLink">
                    <i class="bi bi-cup-hot-fill"></i>
                    Mase Café
                </a>
        
                <div class="m-0">
                    <button id="logout_button" type="button" class="btn btn-outline-light btn-sm">
                        Logout
                    </button>
                </div>
        
            </div>
        </nav>
        
        <div class="container-fluid">
            <div class="row">
                <nav class="col-md-2 sidebar" aria-label="Main Sidebar">
                    <ul class="nav flex-column">
                        <li class="nav-item role-restricted" data-allowed="MANAGER">
                            <a class="nav-link" href="#" id="nav-dashboard">
                                <i class="bi bi-speedometer2 me-2"></i>
                                Dashboard 
                            </a> 
                            <a class="nav-link" href="#" id="nav-users">
                                <i class="bi bi-person-fill-gear"></i>
                                Users
                            </a>
                            <a class="nav-link" href="#" id="nav-menus">
                                <i class="bi bi-journal-text me-2"></i>
                                Menu Management
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
    `;

    $("#logout_button").click(function () {
        localStorage.removeItem("token");
        globalThis.location.reload();
    });

    $("#nav-dashboard").click(function (e) {
        e.preventDefault();
        renderManagerDashboard($("#dashboard-root")[0]);
    });
    $("#nav-users").click(function (e) {
        e.preventDefault();
        renderUserManagement($("#dashboard-root")[0]);
    });
    $("#nav-menus").click(function (e) {
        e.preventDefault();
        renderMenuManagement($("#dashboard-root")[0]);
    });

    renderManagerDashboard($("#dashboard-root")[0]);
}