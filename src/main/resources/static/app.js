import { login } from './dashboards/login.js';
import { renderManagerDashboard } from './dashboards/managerDashboard.js';

let currentUser = null;

document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    $.get('/api/user/me')
        .done(user => {
            currentUser = user;
            updateHeader(user);
            configureSidebar(user.role);

            renderManagerDashboard();
            setupNavigation();
        })
        .fail(() => globalThis.location.href = '/login');
}

function configureSidebar(userRole) {
    const role = userRole?.replaceAll('ROLE_', '') ?? "";

    document.querySelectorAll('.role-restricted').forEach(item => {
        const allowedRoles = item.dataset.allowed;
        if (allowedRoles?.includes(role)) $(item).show();
        else $(item).hide();
    });
}

function setupNavigation() {
    document.querySelectorAll('.sidebar .nav-link')
        .forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                navigateTo(link.id);
            });
        });

    const brandLink = document.getElementById('brandLink');
    if (brandLink) {
        brandLink.addEventListener('click', (e) => {
            e.preventDefault();

            // reset active link properly
            document.querySelectorAll('.sidebar .nav-link')
                .forEach(l => l.classList.remove('active'));
            document.getElementById('nav-dashboard')?.classList.add('active');

            renderManagerDashboard();
        });
    }
}

function navigateTo(navId) {
    document.querySelectorAll('.sidebar .nav-link')
        .forEach(l => l.classList.remove('active'));

    document.getElementById(navId)?.classList.add('active');

    if (navId === 'nav-dashboard') {
        renderManagerDashboard();
    }
}
