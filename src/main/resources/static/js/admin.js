/**
 * Admin dashboard related JavaScript functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard charts
    initDashboardCharts();
    
    // Initialize admin tables
    initAdminTables();
    
    // Initialize form validations
    initAdminForms();
    
    // Initialize date range pickers
    initDateRangePickers();
});

/**
 * Initialize dashboard charts if Chart.js is available
 */
function initDashboardCharts() {
    // Check if we're on admin dashboard page
    const dashboardPage = document.querySelector('.admin-dashboard');
    if (!dashboardPage) return;
    
    // Add Chart.js if needed
    if (typeof Chart === 'undefined') {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/chart.js';
        script.onload = createDashboardCharts;
        document.head.appendChild(script);
    } else {
        createDashboardCharts();
    }
}

/**
 * Create dashboard charts
 */
function createDashboardCharts() {
    // Appointments chart
    const appointmentsChart = document.getElementById('appointmentsChart');
    if (appointmentsChart) {
        try {
            const appointmentsData = JSON.parse(appointmentsChart.dataset.appointments || '{}');
            const months = Object.keys(appointmentsData);
            const counts = Object.values(appointmentsData);
            
            new Chart(appointmentsChart, {
                type: 'bar',
                data: {
                    labels: months,
                    datasets: [{
                        label: 'Monthly Appointments',
                        data: counts,
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
        } catch (error) {
            console.error('Error creating appointments chart:', error);
        }
    }
    
    // Revenue chart
    const revenueChart = document.getElementById('revenueChart');
    if (revenueChart) {
        try {
            const revenueData = JSON.parse(revenueChart.dataset.revenue || '{}');
            const months = Object.keys(revenueData);
            const values = Object.values(revenueData);
            
            new Chart(revenueChart, {
                type: 'line',
                data: {
                    labels: months,
                    datasets: [{
                        label: 'Monthly Revenue ($)',
                        data: values,
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
        } catch (error) {
            console.error('Error creating revenue chart:', error);
        }
    }
    
    // Vaccinations by type chart
    const vaccinationsChart = document.getElementById('vaccinationsChart');
    if (vaccinationsChart) {
        try {
            const vaccineData = JSON.parse(vaccinationsChart.dataset.vaccinations || '{}');
            const vaccines = Object.keys(vaccineData);
            const counts = Object.values(vaccineData);
            
            // Generate colors
            const backgroundColors = vaccines.map((_, i) => {
                const hue = (i * 137) % 360; // Use golden angle approximation for color distribution
                return `hsla(${hue}, 70%, 60%, 0.7)`;
            });
            
            new Chart(vaccinationsChart, {
                type: 'doughnut',
                data: {
                    labels: vaccines,
                    datasets: [{
                        data: counts,
                        backgroundColor: backgroundColors,
                        borderWidth: 1
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
        } catch (error) {
            console.error('Error creating vaccinations chart:', error);
        }
    }
    
    // Feedback chart
    const feedbackChart = document.getElementById('feedbackChart');
    if (feedbackChart) {
        try {
            const feedbackData = JSON.parse(feedbackChart.dataset.feedback || '{}');
            const ratings = Object.keys(feedbackData);
            const counts = Object.values(feedbackData);
            
            new Chart(feedbackChart, {
                type: 'bar',
                data: {
                    labels: ratings.map(r => `${r} Star${r !== '1' ? 's' : ''}`),
                    datasets: [{
                        label: 'Feedback Count',
                        data: counts,
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
        } catch (error) {
            console.error('Error creating feedback chart:', error);
        }
    }
    
    // Reactions chart
    const reactionsChart = document.getElementById('reactionsChart');
    if (reactionsChart) {
        try {
            const reactionsData = JSON.parse(reactionsChart.dataset.reactions || '{}');
            const severityData = reactionsData.reactionsBySeverity || {};
            const severities = Object.keys(severityData);
            const counts = Object.values(severityData);
            
            new Chart(reactionsChart, {
                type: 'pie',
                data: {
                    labels: severities,
                    datasets: [{
                        data: counts,
                        backgroundColor: [
                            'rgba(46, 204, 113, 0.7)',   // Mild - green
                            'rgba(243, 156, 18, 0.7)',   // Moderate - orange
                            'rgba(231, 76, 60, 0.7)'     // Severe - red
                        ],
                        borderColor: [
                            'rgba(46, 204, 113, 1)',
                            'rgba(243, 156, 18, 1)',
                            'rgba(231, 76, 60, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true
                }
            });
        } catch (error) {
            console.error('Error creating reactions chart:', error);
        }
    }
    
    // Vaccination coverage chart
    const coverageChart = document.getElementById('coverageChart');
    if (coverageChart) {
        try {
            const coverageData = JSON.parse(coverageChart.dataset.coverage || '{}');
            const ageGroups = Object.keys(coverageData);
            const percentages = Object.values(coverageData);
            
            new Chart(coverageChart, {
                type: 'bar',
                data: {
                    labels: ageGroups,
                    datasets: [{
                        label: 'Vaccination Coverage (%)',
                        data: percentages,
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
                            max: 100
                        }
                    }
                }
            });
        } catch (error) {
            console.error('Error creating coverage chart:', error);
        }
    }
}

/**
 * Initialize admin tables with sorting, filtering, and pagination
 */
function initAdminTables() {
    const adminTables = document.querySelectorAll('.admin-table');
    if (adminTables.length === 0) return;
    
    adminTables.forEach(table => {
        // Add sorting
        const headers = table.querySelectorAll('th[data-sort]');
        headers.forEach(header => {
            header.addEventListener('click', function() {
                const column = this.dataset.sort;
                sortAdminTable(table, column, this);
            });
        });
        
        // Add search functionality if search input exists
        const searchInput = table.parentElement.querySelector('.table-search');
        if (searchInput) {
            searchInput.addEventListener('input', function() {
                filterAdminTable(table, this.value);
            });
        }
    });
}

/**
 * Sort admin table by column
 * @param {HTMLTableElement} table - The table to sort
 * @param {string} column - The column to sort by
 * @param {HTMLElement} header - The header element
 */
function sortAdminTable(table, column, header) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    // Toggle sort direction
    const isAscending = header.classList.contains('sort-asc');
    
    // Remove sort classes from all headers
    table.querySelectorAll('th').forEach(th => {
        th.classList.remove('sort-asc', 'sort-desc');
    });
    
    // Add sort class to current header
    header.classList.add(isAscending ? 'sort-desc' : 'sort-asc');
    
    // Sort rows
    rows.sort((rowA, rowB) => {
        const cellA = rowA.querySelector(`td[data-${column}]`);
        const cellB = rowB.querySelector(`td[data-${column}]`);
        
        if (!cellA || !cellB) return 0;
        
        let valueA = cellA.dataset[column];
        let valueB = cellB.dataset[column];
        
        // Handle number sorting
        if (!isNaN(valueA) && !isNaN(valueB)) {
            return isAscending 
                ? Number(valueB) - Number(valueA)
                : Number(valueA) - Number(valueB);
        }
        
        // Handle date sorting
        if (column.includes('date') || column.includes('time')) {
            return isAscending 
                ? new Date(valueB) - new Date(valueA)
                : new Date(valueA) - new Date(valueB);
        }
        
        // Handle string sorting
        return isAscending 
            ? valueB.localeCompare(valueA)
            : valueA.localeCompare(valueB);
    });
    
    // Re-append rows in new order
    rows.forEach(row => tbody.appendChild(row));
}

/**
 * Filter admin table by search query
 * @param {HTMLTableElement} table - The table to filter
 * @param {string} query - Search query
 */
function filterAdminTable(table, query) {
    const tbody = table.querySelector('tbody');
    const rows = tbody.querySelectorAll('tr');
    const lowercaseQuery = query.toLowerCase();
    
    let visibleCount = 0;
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        const isVisible = text.includes(lowercaseQuery);
        
        row.style.display = isVisible ? '' : 'none';
        
        if (isVisible) {
            visibleCount++;
        }
    });
    
    // Show no results message if needed
    const noResults = table.parentElement.querySelector('.no-results');
    if (noResults) {
        noResults.style.display = visibleCount === 0 ? 'block' : 'none';
    }
}

/**
 * Initialize admin forms validation
 */
function initAdminForms() {
    // User form validation
    const userForm = document.getElementById('userForm');
    if (userForm) {
        userForm.addEventListener('submit', validateUserForm);
    }
    
    // Vaccine form validation
    const vaccineForm = document.getElementById('vaccineForm');
    if (vaccineForm) {
        vaccineForm.addEventListener('submit', validateVaccineForm);
    }
    
    // Vaccine service form validation
    const serviceForm = document.getElementById('vaccineServiceForm');
    if (serviceForm) {
        serviceForm.addEventListener('submit', validateServiceForm);
    }
}

/**
 * Validate user form
 * @param {Event} e - Form submit event
 */
function validateUserForm(e) {
    const form = e.target;
    const username = form.querySelector('#username');
    const email = form.querySelector('#email');
    const password = form.querySelector('#password');
    const confirmPassword = form.querySelector('#confirmPassword');
    const role = form.querySelector('select[name="role"]');
    
    let isValid = true;
    
    // Clear previous errors
    clearFormErrors(form);
    
    // Validate username
    if (username && !username.value.trim()) {
        addFormError(username, 'Username is required');
        isValid = false;
    }
    
    // Validate email
    if (email && !email.value.trim()) {
        addFormError(email, 'Email is required');
        isValid = false;
    } else if (email && !isValidEmail(email.value)) {
        addFormError(email, 'Invalid email format');
        isValid = false;
    }
    
    // Validate password if it's a new user or password field is filled
    if ((password && password.value) || !form.querySelector('input[name="id"]')) {
        if (password && !password.value) {
            addFormError(password, 'Password is required');
            isValid = false;
        } else if (password && password.value.length < 6) {
            addFormError(password, 'Password must be at least 6 characters');
            isValid = false;
        }
        
        // Check password confirmation
        if (confirmPassword && password.value !== confirmPassword.value) {
            addFormError(confirmPassword, 'Passwords don\'t match');
            isValid = false;
        }
    }
    
    // Validate role
    if (role && !role.value) {
        addFormError(role, 'Role is required');
        isValid = false;
    }
    
    if (!isValid) {
        e.preventDefault();
    }
}

/**
 * Validate vaccine form
 * @param {Event} e - Form submit event
 */
function validateVaccineForm(e) {
    const form = e.target;
    const name = form.querySelector('#name');
    const manufacturer = form.querySelector('#manufacturer');
    const recommendedAgeMonthsStart = form.querySelector('#recommendedAgeMonthsStart');
    const price = form.querySelector('#price');
    
    let isValid = true;
    
    // Clear previous errors
    clearFormErrors(form);
    
    // Validate name
    if (!name.value.trim()) {
        addFormError(name, 'Vaccine name is required');
        isValid = false;
    }
    
    // Validate manufacturer
    if (!manufacturer.value.trim()) {
        addFormError(manufacturer, 'Manufacturer is required');
        isValid = false;
    }
    
    // Validate recommended age
    if (!recommendedAgeMonthsStart.value) {
        addFormError(recommendedAgeMonthsStart, 'Starting age is required');
        isValid = false;
    } else if (isNaN(recommendedAgeMonthsStart.value) || recommendedAgeMonthsStart.value < 0) {
        addFormError(recommendedAgeMonthsStart, 'Starting age must be a positive number');
        isValid = false;
    }
    
    // Validate price
    if (!price.value) {
        addFormError(price, 'Price is required');
        isValid = false;
    } else if (isNaN(price.value) || price.value <= 0) {
        addFormError(price, 'Price must be a positive number');
        isValid = false;
    }
    
    if (!isValid) {
        e.preventDefault();
    }
}

/**
 * Validate vaccine service form
 * @param {Event} e - Form submit event
 */
function validateServiceForm(e) {
    const form = e.target;
    const name = form.querySelector('#name');
    const serviceType = form.querySelector('#serviceType');
    const price = form.querySelector('#price');
    const vaccineCheckboxes = form.querySelectorAll('input[name="vaccineIds"]');
    
    let isValid = true;
    
    // Clear previous errors
    clearFormErrors(form);
    
    // Validate name
    if (!name.value.trim()) {
        addFormError(name, 'Service name is required');
        isValid = false;
    }
    
    // Validate service type
    if (!serviceType.value) {
        addFormError(serviceType, 'Service type is required');
        isValid = false;
    }
    
    // Validate price
    if (!price.value) {
        addFormError(price, 'Price is required');
        isValid = false;
    } else if (isNaN(price.value) || price.value <= 0) {
        addFormError(price, 'Price must be a positive number');
        isValid = false;
    }
    
    // Validate at least one vaccine is selected
    let atLeastOneVaccineSelected = false;
    vaccineCheckboxes.forEach(checkbox => {
        if (checkbox.checked) {
            atLeastOneVaccineSelected = true;
        }
    });
    
    if (!atLeastOneVaccineSelected) {
        const vaccinesContainer = form.querySelector('.vaccines-container') || form;
        const errorDiv = document.createElement('div');
        errorDiv.className = 'form-error';
        errorDiv.textContent = 'Select at least one vaccine';
        vaccinesContainer.appendChild(errorDiv);
        isValid = false;
    }
    
    if (!isValid) {
        e.preventDefault();
    }
}

/**
 * Initialize date range pickers for reports
 */
function initDateRangePickers() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    if (startDateInput && endDateInput) {
        // Set default dates if not already set
        if (!startDateInput.value) {
            const today = new Date();
            const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
            startDateInput.value = formatDateForInput(firstDay);
        }
        
        if (!endDateInput.value) {
            const today = new Date();
            endDateInput.value = formatDateForInput(today);
        }
        
        // Add event listeners
        startDateInput.addEventListener('change', function() {
            // Ensure end date is not before start date
            if (endDateInput.value && new Date(endDateInput.value) < new Date(this.value)) {
                endDateInput.value = this.value;
            }
            
            // Set min date for end date
            endDateInput.min = this.value;
        });
        
        endDateInput.addEventListener('change', function() {
            // Ensure start date is not after end date
            if (startDateInput.value && new Date(startDateInput.value) > new Date(this.value)) {
                startDateInput.value = this.value;
            }
            
            // Set max date for start date
            startDateInput.max = this.value;
        });
        
        // Set initial constraints
        endDateInput.min = startDateInput.value;
        startDateInput.max = endDateInput.value;
    }
}

/**
 * Format date for date input value (YYYY-MM-DD)
 * @param {Date} date - Date to format
 * @returns {string} Formatted date string
 */
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * Add error message to form field
 * @param {HTMLElement} field - Form field
 * @param {string} message - Error message
 */
function addFormError(field, message) {
    const formGroup = field.closest('.form-group');
    formGroup.classList.add('has-error');
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'form-error';
    errorDiv.textContent = message;
    
    formGroup.appendChild(errorDiv);
}

/**
 * Clear all error messages from form
 * @param {HTMLFormElement} form - Form element
 */
function clearFormErrors(form) {
    form.querySelectorAll('.form-error').forEach(error => {
        error.parentNode.removeChild(error);
    });
    
    form.querySelectorAll('.has-error').forEach(formGroup => {
        formGroup.classList.remove('has-error');
    });
}

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} Whether email is valid
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Export report data to CSV
 * @param {HTMLElement} button - Export button
 */
function exportReportToCSV(button) {
    const reportType = button.dataset.reportType;
    const dataTable = document.querySelector(`.${reportType}-table`);
    
    if (!dataTable) {
        alert('No data available to export');
        return;
    }
    
    let csvContent = 'data:text/csv;charset=utf-8,';
    
    // Get headers
    const headers = Array.from(dataTable.querySelectorAll('thead th'))
        .map(th => th.textContent.trim());
    csvContent += headers.join(',') + '\n';
    
    // Get rows
    const rows = dataTable.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const cells = Array.from(row.querySelectorAll('td'))
            .map(td => {
                // Escape quotes and wrap with quotes to handle commas in data
                let data = td.textContent.trim();
                data = data.replace(/"/g, '""');
                return `"${data}"`;
            });
        csvContent += cells.join(',') + '\n';
    });
    
    // Create download link
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `${reportType}_report_${formatDateForFile(new Date())}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/**
 * Format date for file name (YYYYMMDD)
 * @param {Date} date - Date to format
 * @returns {string} Formatted date string
 */
function formatDateForFile(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}${month}${day}`;
}
