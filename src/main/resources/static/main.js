import {renderLoginPage} from "./js/dashboards/login.js";
import {renderDashboard} from "./js/dashboards/dashboard.js";

$(document).ready(() => {
    const app = document.getElementById('app');
    // Check if token is in local storage
    const token = localStorage.getItem("token");
    if (token && token !== "null" && token !== "undefined") {
       renderDashboard(app);
   } else {
       renderLoginPage(app);
   }
});