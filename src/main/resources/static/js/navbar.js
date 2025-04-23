function createNavbar() {
    const navbar = document.createElement('nav');
    navbar.className = 'navbar navbar-expand-lg navbar-dark bg-dark';
    navbar.innerHTML = `
        <div class="container-fluid">
            <a class="navbar-brand" href="/">TehriMap</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/occurrence.html">Report Occurrence</a>
                    </li>
                    ${isAdmin() ? `
                        <li class="nav-item">
                            <a class="nav-link" href="/admin.html">Admin Dashboard</a>
                        </li>
                    ` : ''}
                </ul>
                <div class="d-flex">
                    ${isAuthenticated() ? `
                        <span class="navbar-text me-3">${getUserName()}</span>
                        <button class="btn btn-outline-light" onclick="logout()">Logout</button>
                    ` : `
                        <button class="btn btn-outline-light me-2" onclick="window.location.href='/login.html'">Login</button>
                        <button class="btn btn-outline-light" onclick="window.location.href='/register.html'">Register</button>
                    `}
                </div>
            </div>
        </div>
    `;
    document.body.insertBefore(navbar, document.body.firstChild);
}

function isAuthenticated() {
    return sessionStorage.getItem('token') !== null;
}

function isAdmin() {
    return sessionStorage.getItem('role') === 'ADMIN';
}

function getUserName() {
    return sessionStorage.getItem('username') || 'User';
}

function logout() {
    sessionStorage.clear();
    window.location.href = '/login.html';
}

// Create navbar when the script loads
document.addEventListener('DOMContentLoaded', createNavbar); 