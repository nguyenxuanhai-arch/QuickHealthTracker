/**
 * Child management related JavaScript functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize child form
    initChildForm();
    
    // Initialize vaccination history
    initVaccinationHistory();
    
    // Initialize child profile tabs
    initChildProfileTabs();
});

/**
 * Initialize child form validation and submission
 */
function initChildForm() {
    const childForm = document.getElementById('childForm');
    if (!childForm) return;
    
    childForm.addEventListener('submit', function(e) {
        // Validate the form
        if (!validateChildForm()) {
            e.preventDefault();
            return false;
        }
    });
    
    // Setup child age calculation
    const dobInput = document.getElementById('dateOfBirth');
    if (dobInput) {
        dobInput.addEventListener('change', calculateChildAge);
    }
}

/**
 * Validate child form fields
 * @returns {boolean} Whether form is valid
 */
function validateChildForm() {
    const fullName = document.getElementById('fullName');
    const dateOfBirth = document.getElementById('dateOfBirth');
    const gender = document.querySelector('input[name="gender"]:checked');
    
    let isValid = true;
    
    // Clear previous error messages
    clearErrorMessages();
    
    // Validate full name
    if (!fullName.value.trim()) {
        showFieldError(fullName, 'Child\'s full name is required');
        isValid = false;
    }
    
    // Validate date of birth
    if (!dateOfBirth.value) {
        showFieldError(dateOfBirth, 'Date of birth is required');
        isValid = false;
    } else {
        const dob = new Date(dateOfBirth.value);
        const now = new Date();
        
        if (dob > now) {
            showFieldError(dateOfBirth, 'Date of birth cannot be in the future');
            isValid = false;
        }
    }
    
    // Validate gender
    if (!gender) {
        const genderContainer = document.querySelector('.gender-container');
        showContainerError(genderContainer, 'Please select a gender');
        isValid = false;
    }
    
    return isValid;
}

/**
 * Calculate and display child's age based on date of birth
 */
function calculateChildAge() {
    const dobInput = document.getElementById('dateOfBirth');
    const ageDisplay = document.getElementById('childAge');
    
    if (!dobInput || !ageDisplay) return;
    
    if (dobInput.value) {
        const dob = new Date(dobInput.value);
        const now = new Date();
        
        let years = now.getFullYear() - dob.getFullYear();
        let months = now.getMonth() - dob.getMonth();
        
        if (months < 0) {
            years--;
            months += 12;
        }
        
        // Calculate days
        let days = now.getDate() - dob.getDate();
        if (days < 0) {
            months--;
            // Get the last day of previous month
            const lastMonth = new Date(now.getFullYear(), now.getMonth(), 0);
            days += lastMonth.getDate();
        }
        
        let ageStr = '';
        if (years > 0) {
            ageStr += `${years} year${years !== 1 ? 's' : ''}`;
        }
        
        if (months > 0) {
            if (ageStr) ageStr += ', ';
            ageStr += `${months} month${months !== 1 ? 's' : ''}`;
        }
        
        if (years === 0 && months === 0) {
            ageStr = `${days} day${days !== 1 ? 's' : ''}`;
        }
        
        ageDisplay.textContent = ageStr;
        ageDisplay.parentElement.style.display = 'block';
    } else {
        ageDisplay.parentElement.style.display = 'none';
    }
}

/**
 * Initialize vaccination history table sorting and filtering
 */
function initVaccinationHistory() {
    const vaccinationTable = document.getElementById('vaccinationHistoryTable');
    if (!vaccinationTable) return;
    
    // Add sorting functionality
    const headers = vaccinationTable.querySelectorAll('th[data-sort]');
    headers.forEach(header => {
        header.addEventListener('click', function() {
            const column = this.dataset.sort;
            sortTable(vaccinationTable, column, this);
        });
    });
    
    // Add filter functionality
    const filterInput = document.getElementById('vaccineFilter');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            filterVaccinationTable(this.value);
        });
    }
    
    // Add status filter
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            filterVaccinationByStatus(this.value);
        });
    }
}

/**
 * Sort vaccination history table
 * @param {HTMLTableElement} table - The table to sort
 * @param {string} column - The column to sort by
 * @param {HTMLElement} header - The header element
 */
function sortTable(table, column, header) {
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
        
        // Handle date sorting
        if (column === 'date') {
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
 * Filter vaccination table by vaccine name
 * @param {string} query - Search query
 */
function filterVaccinationTable(query) {
    const table = document.getElementById('vaccinationHistoryTable');
    if (!table) return;
    
    const rows = table.querySelectorAll('tbody tr');
    const lowercaseQuery = query.toLowerCase();
    
    rows.forEach(row => {
        const vaccineName = row.querySelector('td[data-vaccine]').dataset.vaccine.toLowerCase();
        const matchesSearch = vaccineName.includes(lowercaseQuery);
        
        // Check if it also matches the status filter
        const statusFilter = document.getElementById('statusFilter');
        let matchesStatus = true;
        
        if (statusFilter && statusFilter.value !== 'all') {
            const rowStatus = row.querySelector('td[data-status]').dataset.status;
            matchesStatus = rowStatus === statusFilter.value;
        }
        
        row.style.display = (matchesSearch && matchesStatus) ? '' : 'none';
    });
}

/**
 * Filter vaccination table by status
 * @param {string} status - Status to filter by
 */
function filterVaccinationByStatus(status) {
    const table = document.getElementById('vaccinationHistoryTable');
    if (!table) return;
    
    const rows = table.querySelectorAll('tbody tr');
    
    rows.forEach(row => {
        const rowStatus = row.querySelector('td[data-status]').dataset.status;
        const matchesStatus = status === 'all' || rowStatus === status;
        
        // Check if it also matches the search filter
        const searchFilter = document.getElementById('vaccineFilter');
        let matchesSearch = true;
        
        if (searchFilter && searchFilter.value) {
            const vaccineName = row.querySelector('td[data-vaccine]').dataset.vaccine.toLowerCase();
            matchesSearch = vaccineName.includes(searchFilter.value.toLowerCase());
        }
        
        row.style.display = (matchesStatus && matchesSearch) ? '' : 'none';
    });
}

/**
 * Initialize child profile tabs
 */
function initChildProfileTabs() {
    const tabLinks = document.querySelectorAll('[data-toggle="tab"]');
    if (tabLinks.length === 0) return;
    
    tabLinks.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get target tab
            const targetId = this.getAttribute('href').substring(1);
            const targetTab = document.getElementById(targetId);
            
            // Hide all tabs
            document.querySelectorAll('.tab-pane').forEach(pane => {
                pane.classList.remove('active');
            });
            
            // Remove active class from all tab links
            tabLinks.forEach(link => {
                link.classList.remove('active');
            });
            
            // Show target tab and mark link as active
            targetTab.classList.add('active');
            this.classList.add('active');
            
            // Update URL hash
            window.location.hash = targetId;
        });
    });
    
    // Show tab from URL hash
    const hash = window.location.hash;
    if (hash) {
        const tabLink = document.querySelector(`[data-toggle="tab"][href="${hash}"]`);
        if (tabLink) {
            tabLink.click();
        }
    } else {
        // Show first tab by default
        tabLinks[0].click();
    }
}

/**
 * Show error message for a field
 * @param {HTMLElement} field - The input field with error
 * @param {string} message - Error message
 */
function showFieldError(field, message) {
    const formGroup = field.closest('.form-group');
    formGroup.classList.add('has-error');
    
    const errorSpan = document.createElement('span');
    errorSpan.className = 'error-message';
    errorSpan.textContent = message;
    
    formGroup.appendChild(errorSpan);
}

/**
 * Show error message for a container
 * @param {HTMLElement} container - The container with error
 * @param {string} message - Error message
 */
function showContainerError(container, message) {
    container.classList.add('has-error');
    
    const errorSpan = document.createElement('span');
    errorSpan.className = 'error-message';
    errorSpan.textContent = message;
    
    container.appendChild(errorSpan);
}

/**
 * Clear all error messages
 */
function clearErrorMessages() {
    // Remove all error-message elements
    document.querySelectorAll('.error-message').forEach(error => {
        error.parentNode.removeChild(error);
    });
    
    // Remove has-error class from form groups
    document.querySelectorAll('.has-error').forEach(group => {
        group.classList.remove('has-error');
    });
}
