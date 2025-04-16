/**
 * Authentication related JavaScript functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize login form
    initLoginForm();
    
    // Initialize registration form
    initRegistrationForm();
    
    // Handle logout
    initLogout();
    
    // Check token in localStorage
    checkAuthToken();
});

/**
 * Initialize login form submission
 */
function initLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (!loginForm) return;
    
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Get form inputs
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        // Client-side validation
        if (!username || !password) {
            showError('Please enter both username and password.');
            return;
        }
        
        // Submit the form - no API call needed since we're using Spring Security form login
        this.submit();
    });
}

/**
 * Initialize registration form submission
 */
function initRegistrationForm() {
    const registrationForm = document.getElementById('registrationForm');
    if (!registrationForm) return;
    
    registrationForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Get form inputs
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const fullName = document.getElementById('fullName').value;
        
        // Client-side validation
        if (!username || !email || !password || !confirmPassword || !fullName) {
            showError('Please fill in all required fields.');
            return;
        }
        
        if (password !== confirmPassword) {
            showError('Passwords do not match.');
            return;
        }
        
        if (password.length < 6) {
            showError('Password must be at least 6 characters long.');
            return;
        }
        
        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showError('Please enter a valid email address.');
            return;
        }
        
        // Submit the form - no API call needed since we're using traditional form submission
        this.submit();
    });
}

/**
 * Initialize logout functionality
 */
function initLogout() {
    const logoutLink = document.querySelector('a[href="/logout"]');
    if (!logoutLink) return;
    
    logoutLink.addEventListener('click', function(e) {
        // Remove any JWT token from localStorage
        localStorage.removeItem('authToken');
    });
}

/**
 * Check if auth token exists in localStorage
 */
function checkAuthToken() {
    const token = localStorage.getItem('authToken');
    if (token) {
        // Token exists, user might be logged in via JWT
        // This is for API access, regular sessions are handled by Spring Security
        
        // Add token to all API requests
        setupAuthInterceptor(token);
    }
}

/**
 * Setup interceptor to add auth token to API requests
 * @param {string} token - JWT token
 */
function setupAuthInterceptor(token) {
    // Intercept fetch requests to add Authorization header
    const originalFetch = window.fetch;
    window.fetch = function(url, options = {}) {
        // Only add header for API endpoints
        if (url.toString().includes('/api/')) {
            if (!options.headers) {
                options.headers = {};
            }
            
            // Convert Headers object to plain object if needed
            if (options.headers instanceof Headers) {
                const plainHeaders = {};
                for (const [key, value] of options.headers.entries()) {
                    plainHeaders[key] = value;
                }
                options.headers = plainHeaders;
            }
            
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        
        return originalFetch(url, options);
    };
}

/**
 * Show error message in login/registration forms
 * @param {string} message - Error message to display
 */
function showError(message) {
    let errorElement = document.querySelector('.auth-error');
    
    if (!errorElement) {
        // Create error element if it doesn't exist
        errorElement = document.createElement('div');
        errorElement.className = 'alert alert-danger auth-error';
        
        // Insert at top of form
        const form = document.querySelector('form');
        form.insertBefore(errorElement, form.firstChild);
    }
    
    errorElement.textContent = message;
    
    // Scroll to error
    errorElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

/**
 * Login via API (for SPA functionality if needed)
 * @param {string} username - Username
 * @param {string} password - Password
 * @returns {Promise} Promise with login result
 */
async function apiLogin(username, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        if (!response.ok) {
            throw new Error('Login failed');
        }
        
        const data = await response.json();
        
        // Store token in localStorage
        localStorage.setItem('authToken', data.token);
        
        // Setup auth interceptor
        setupAuthInterceptor(data.token);
        
        return data;
    } catch (error) {
        console.error('Login error:', error);
        throw error;
    }
}

/**
 * Register via API (for SPA functionality if needed)
 * @param {Object} userData - User registration data
 * @returns {Promise} Promise with registration result
 */
async function apiRegister(userData) {
    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData || 'Registration failed');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Registration error:', error);
        throw error;
    }
}
