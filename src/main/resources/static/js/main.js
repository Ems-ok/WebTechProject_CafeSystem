import { renderLoginPage } from "./dashboards/login.js";
import { renderDashboard } from "./dashboards/dashboard.js";

$(document).ready(function () {

    const app = $("#app")[0]; // jQuery selector

    // Check if token exists in localStorage
    const token = localStorage.getItem("token");

    if (token && token !== "null" && token !== "undefined") {
        renderDashboard(app);
    } else {
        renderLoginPage(app);
    }

});