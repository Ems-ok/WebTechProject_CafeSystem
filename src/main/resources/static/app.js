import { renderManagerDashboard } from './dashboards/managerDashboard.js';
import { renderUserManagement } from './dashboards/userManagement.js';

let currentUser = null;

$(document).ready(function () {
    initApp();
});

function initApp() {

    $.get('/api/user/me')
        .done(function (user) {

            currentUser = user;

            updateHeader(user);
            configureSidebar(user.role);

            renderManagerDashboard();
            setupNavigation();

        })
        .fail(function () {
            window.location.href = '/login';
        });
}

function configureSidebar(userRole) {

    const role = userRole?.replaceAll('ROLE_', '') ?? "";

    $('.role-restricted').each(function () {

        const allowedRoles = $(this).data('allowed');

        if (allowedRoles && allowedRoles.includes(role)) {
            $(this).show();
        } else {
            $(this).hide();
        }

    });
}

function setupNavigation() {

    $('.sidebar .nav-link').on('click', function (e) {

        e.preventDefault();

        const navId = $(this).attr('id');

        navigateTo(navId);

    });

    $('#brandLink').on('click', function (e) {

        e.preventDefault();

        $('.sidebar .nav-link').removeClass('active');

        $('#nav-dashboard').addClass('active');

        renderManagerDashboard();
        renderUserManagement();

    });

}

function navigateTo(navId) {

    $('.sidebar .nav-link').removeClass('active');

    $('#' + navId).addClass('active');

    if (navId === 'nav-dashboard') {
        renderManagerDashboard();
        renderUserManagement();
    }

}