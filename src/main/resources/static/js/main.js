/**
 * Main JavaScript file for the Vaccine Management System
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all tooltips
    initializeTooltips();
    
    // Initialize date pickers
    initializeDatepickers();
    
    // Initialize alert auto-dismiss
    initializeAlertDismiss();
    
    // Add event listeners for delete confirmations
    initializeDeleteConfirmations();
});

/**
 * Initialize tooltips for elements with data-toggle="tooltip" attribute
 */
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-toggle="tooltip"]'));
    tooltipTriggerList.forEach(function(tooltipTriggerEl) {
        const title = tooltipTriggerEl.getAttribute('title');
        
        tooltipTriggerEl.addEventListener('mouseover', function() {
            const tooltip = document.createElement('div');
            tooltip.classList.add('tooltip');
            tooltip.innerText = title;
            document.body.appendChild(tooltip);
            
            const rect = tooltipTriggerEl.getBoundingClientRect();
            tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
            tooltip.style.top = rect.top - tooltip.offsetHeight - 10 + window.scrollY + 'px';
            
            setTimeout(() => tooltip.classList.add('show'), 10);
            
            tooltipTriggerEl.addEventListener('mouseout', function handler() {
                tooltip.classList.remove('show');
                setTimeout(() => {
                    document.body.removeChild(tooltip);
                }, 300);
                tooltipTriggerEl.removeEventListener('mouseout', handler);
            });
        });
    });
}

/**
 * Initialize date pickers for elements with class 'datepicker'
 */
function initializeDatepickers() {
    const datepickers = document.querySelectorAll('.datepicker');
    datepickers.forEach(datepicker => {
        datepicker.type = 'date';
        
        // Set min date to today for future dates
        if (datepicker.classList.contains('future-date')) {
            const today = new Date().toISOString().split('T')[0];
            datepicker.min = today;
        }
    });
}

/**
 * Initialize auto-dismiss for alert messages after 5 seconds
 */
function initializeAlertDismiss() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 500);
        }, 5000);
        
        // Add close button functionality
        const closeBtn = alert.querySelector('.close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                alert.style.opacity = '0';
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.parentNode.removeChild(alert);
                    }
                }, 500);
            });
        }
    });
}

/**
 * Initialize confirmation prompts for delete actions
 */
function initializeDeleteConfirmations() {
    const deleteButtons = document.querySelectorAll('.btn-delete, .delete-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                e.preventDefault();
                return false;
            }
        });
    });
}

/**
 * Format date as YYYY-MM-DD
 * @param {Date} date - The date to format
 * @returns {string} Formatted date string
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * Format date as DD/MM/YYYY
 * @param {string} dateString - The date string to format (YYYY-MM-DD)
 * @returns {string} Formatted date string
 */
function formatDateDisplay(dateString) {
    if (!dateString) return '';
    const parts = dateString.split('-');
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

/**
 * Format datetime string to local date and time
 * @param {string} datetimeString - The datetime string to format
 * @returns {string} Formatted date and time string
 */
function formatDateTime(datetimeString) {
    if (!datetimeString) return '';
    
    const date = new Date(datetimeString);
    const options = { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
    };
    
    return date.toLocaleDateString('en-US', options);
}

/**
 * Format currency with 2 decimal places
 * @param {number} amount - The amount to format
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}

/**
 * Handler for when tab content should be shown based on URL hash
 */
function handleTabFromHash() {
    const hash = window.location.hash;
    if (hash) {
        const tabId = hash.substring(1);
        const tabLink = document.querySelector(`[data-toggle="tab"][href="#${tabId}"]`);
        if (tabLink) {
            tabLink.click();
        }
    }
}

/**
 * Toggle visibility of a collapsible element
 * @param {string} targetId - The ID of the element to toggle
 */
function toggleCollapse(targetId) {
    const target = document.getElementById(targetId);
    if (target) {
        if (target.style.display === 'none' || target.style.display === '') {
            target.style.display = 'block';
        } else {
            target.style.display = 'none';
        }
    }
}

/**
 * Validate a form and show error messages
 * @param {HTMLFormElement} form - The form to validate
 * @returns {boolean} Whether the form is valid
 */
function validateForm(form) {
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            
            // Add error class to form group
            const formGroup = field.closest('.form-group');
            if (formGroup) {
                formGroup.classList.add('has-error');
            }
            
            // Show error message
            let errorMessage = field.getAttribute('data-error-message') || 'This field is required';
            let errorElement = formGroup.querySelector('.error-message');
            
            if (!errorElement) {
                errorElement = document.createElement('span');
                errorElement.className = 'error-message';
                formGroup.appendChild(errorElement);
            }
            
            errorElement.textContent = errorMessage;
        } else {
            // Remove error class and message if field is valid
            const formGroup = field.closest('.form-group');
            if (formGroup) {
                formGroup.classList.remove('has-error');
                const errorElement = formGroup.querySelector('.error-message');
                if (errorElement) {
                    errorElement.remove();
                }
            }
        }
    });
    
    return isValid;
}

// Check if charts should be initialized (for dashboard pages)
if (document.getElementById('appointmentsChart') || document.getElementById('revenueChart')) {
    // Add Chart.js from CDN if needed
    if (typeof Chart === 'undefined') {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/chart.js';
        script.onload = initializeCharts;
        document.head.appendChild(script);
    } else {
        initializeCharts();
    }
}

/**
 * Initialize charts for dashboard
 */
function initializeCharts() {
    // Check for appointments chart
    const appointmentsCtx = document.getElementById('appointmentsChart');
    if (appointmentsCtx) {
        // Get data from data attribute or use placeholder data
        let appointmentsData = {};
        
        try {
            appointmentsData = JSON.parse(appointmentsCtx.getAttribute('data-appointments') || '{}');
        } catch (e) {
            console.error('Error parsing appointments data:', e);
            appointmentsData = {
                'JANUARY': 10, 'FEBRUARY': 15, 'MARCH': 20, 'APRIL': 25, 
                'MAY': 30, 'JUNE': 35, 'JULY': 30, 'AUGUST': 25, 
                'SEPTEMBER': 20, 'OCTOBER': 15, 'NOVEMBER': 10, 'DECEMBER': 5
            };
        }
        
        const months = Object.keys(appointmentsData);
        const appointmentCounts = Object.values(appointmentsData);
        
        new Chart(appointmentsCtx, {
            type: 'bar',
            data: {
                labels: months,
                datasets: [{
                    label: 'Appointments',
                    data: appointmentCounts,
                    backgroundColor: 'rgba(52, 152, 219, 0.7)',
                    borderColor: 'rgba(52, 152, 219, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    }
    
    // Check for revenue chart
    const revenueCtx = document.getElementById('revenueChart');
    if (revenueCtx) {
        // Get data from data attribute or use placeholder data
        let revenueData = {};
        
        try {
            revenueData = JSON.parse(revenueCtx.getAttribute('data-revenue') || '{}');
        } catch (e) {
            console.error('Error parsing revenue data:', e);
            revenueData = {
                'JANUARY': 1000, 'FEBRUARY': 1500, 'MARCH': 2000, 'APRIL': 2500, 
                'MAY': 3000, 'JUNE': 3500, 'JULY': 3000, 'AUGUST': 2500, 
                'SEPTEMBER': 2000, 'OCTOBER': 1500, 'NOVEMBER': 1000, 'DECEMBER': 500
            };
        }
        
        const months = Object.keys(revenueData);
        const revenueCounts = Object.values(revenueData);
        
        new Chart(revenueCtx, {
            type: 'line',
            data: {
                labels: months,
                datasets: [{
                    label: 'Revenue ($)',
                    data: revenueCounts,
                    fill: false,
                    backgroundColor: 'rgba(46, 204, 113, 0.7)',
                    borderColor: 'rgba(46, 204, 113, 1)',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
    
    // Check for vaccinations chart
    const vaccinationsCtx = document.getElementById('vaccinationsChart');
    if (vaccinationsCtx) {
        // Get data from data attribute or use placeholder data
        let vaccinationsData = {};
        
        try {
            vaccinationsData = JSON.parse(vaccinationsCtx.getAttribute('data-vaccinations') || '{}');
        } catch (e) {
            console.error('Error parsing vaccinations data:', e);
            vaccinationsData = {
                'Polio': 120,
                'MMR': 100,
                'DTaP': 85,
                'Hepatitis B': 70,
                'Influenza': 50
            };
        }
        
        const vaccineTypes = Object.keys(vaccinationsData);
        const vaccineCounts = Object.values(vaccinationsData);
        
        // Generate random colors for each vaccine type
        const backgroundColors = vaccineTypes.map(() => 
            `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.7)`
        );
        
        new Chart(vaccinationsCtx, {
            type: 'pie',
            data: {
                labels: vaccineTypes,
                datasets: [{
                    data: vaccineCounts,
                    backgroundColor: backgroundColors
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'right'
                    }
                }
            }
        });
    }
    
    // Check for feedback chart
    const feedbackCtx = document.getElementById('feedbackChart');
    if (feedbackCtx) {
        // Get data from data attribute or use placeholder data
        let feedbackData = {};
        
        try {
            feedbackData = JSON.parse(feedbackCtx.getAttribute('data-feedback') || '{}');
        } catch (e) {
            console.error('Error parsing feedback data:', e);
            feedbackData = {
                '1': 5,
                '2': 10,
                '3': 20,
                '4': 40,
                '5': 25
            };
        }
        
        const ratings = Object.keys(feedbackData);
        const ratingCounts = Object.values(feedbackData);
        
        new Chart(feedbackCtx, {
            type: 'bar',
            data: {
                labels: ratings.map(r => `${r} Star`),
                datasets: [{
                    label: 'Feedback Count',
                    data: ratingCounts,
                    backgroundColor: [
                        'rgba(231, 76, 60, 0.7)',    // 1 star - red
                        'rgba(243, 156, 18, 0.7)',   // 2 star - orange
                        'rgba(241, 196, 15, 0.7)',   // 3 star - yellow
                        'rgba(46, 204, 113, 0.7)',   // 4 star - green
                        'rgba(39, 174, 96, 0.7)'     // 5 star - dark green
                    ],
                    borderColor: [
                        'rgba(231, 76, 60, 1)',
                        'rgba(243, 156, 18, 1)',
                        'rgba(241, 196, 15, 1)',
                        'rgba(46, 204, 113, 1)',
                        'rgba(39, 174, 96, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    }
}
