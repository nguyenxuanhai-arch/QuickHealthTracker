/**
 * Appointment management related JavaScript functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize appointment form
    initAppointmentForm();
    
    // Initialize appointment list actions
    initAppointmentActions();
    
    // Initialize appointment filtering
    initAppointmentFiltering();
});

/**
 * Initialize appointment form validation and submission
 */
function initAppointmentForm() {
    const appointmentForm = document.getElementById('appointmentForm');
    if (!appointmentForm) return;
    
    appointmentForm.addEventListener('submit', function(e) {
        // Validate the form
        if (!validateAppointmentForm()) {
            e.preventDefault();
            return false;
        }
    });
    
    // Initialize child selection
    const childSelect = document.getElementById('childId');
    if (childSelect) {
        childSelect.addEventListener('change', updateChildInfo);
    }
    
    // Initialize service selection
    const serviceSelect = document.getElementById('vaccineServiceId');
    if (serviceSelect) {
        serviceSelect.addEventListener('change', updateServiceInfo);
    }
}

/**
 * Validate appointment form fields
 * @returns {boolean} Whether form is valid
 */
function validateAppointmentForm() {
    const childId = document.getElementById('childId');
    const vaccineServiceId = document.getElementById('vaccineServiceId');
    const appointmentDate = document.getElementById('appointmentDate');
    const appointmentTime = document.getElementById('appointmentTime');
    const paymentMethod = document.getElementById('paymentMethod');
    
    let isValid = true;
    
    // Clear previous error messages
    clearErrorMessages();
    
    // Validate child selection
    if (!childId.value) {
        showFieldError(childId, 'Please select a child');
        isValid = false;
    }
    
    // Validate service selection
    if (!vaccineServiceId.value) {
        showFieldError(vaccineServiceId, 'Please select a vaccination service');
        isValid = false;
    }
    
    // Validate appointment date
    if (!appointmentDate.value) {
        showFieldError(appointmentDate, 'Please select an appointment date');
        isValid = false;
    } else {
        const selectedDate = new Date(appointmentDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (selectedDate < today) {
            showFieldError(appointmentDate, 'Appointment date cannot be in the past');
            isValid = false;
        }
    }
    
    // Validate appointment time
    if (!appointmentTime.value) {
        showFieldError(appointmentTime, 'Please select an appointment time');
        isValid = false;
    }
    
    // Validate payment method
    if (!paymentMethod.value) {
        showFieldError(paymentMethod, 'Please select a payment method');
        isValid = false;
    }
    
    return isValid;
}

/**
 * Update child information when selection changes
 */
function updateChildInfo() {
    const childId = document.getElementById('childId').value;
    const childInfoContainer = document.getElementById('childInfo');
    
    if (!childId || !childInfoContainer) return;
    
    // Show loading indicator
    childInfoContainer.innerHTML = '<p>Loading child information...</p>';
    childInfoContainer.style.display = 'block';
    
    // Fetch child info from API
    fetch(`/api/children/${childId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load child information');
            }
            return response.json();
        })
        .then(child => {
            // Display child info
            const dobDate = new Date(child.dateOfBirth);
            const formattedDob = dobDate.toLocaleDateString('en-US', { 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            });
            
            childInfoContainer.innerHTML = `
                <div class="child-info-card">
                    <p><strong>Name:</strong> ${child.fullName}</p>
                    <p><strong>Date of Birth:</strong> ${formattedDob}</p>
                    <p><strong>Gender:</strong> ${child.gender}</p>
                    ${child.allergies ? `<p><strong>Allergies:</strong> ${child.allergies}</p>` : ''}
                    ${child.medicalConditions ? `<p><strong>Medical Conditions:</strong> ${child.medicalConditions}</p>` : ''}
                </div>
            `;
        })
        .catch(error => {
            console.error('Error fetching child info:', error);
            childInfoContainer.innerHTML = '<p class="text-danger">Failed to load child information</p>';
        });
}

/**
 * Update service information when selection changes
 */
function updateServiceInfo() {
    const serviceId = document.getElementById('vaccineServiceId').value;
    const serviceInfoContainer = document.getElementById('serviceInfo');
    const priceContainer = document.getElementById('price');
    
    if (!serviceId || !serviceInfoContainer) return;
    
    // Show loading indicator
    serviceInfoContainer.innerHTML = '<p>Loading service information...</p>';
    serviceInfoContainer.style.display = 'block';
    
    // Fetch service info from API
    fetch(`/api/admin/vaccine-services/${serviceId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load service information');
            }
            return response.json();
        })
        .then(service => {
            // Display service info
            serviceInfoContainer.innerHTML = `
                <div class="service-info-card">
                    <p><strong>Name:</strong> ${service.name}</p>
                    <p><strong>Type:</strong> ${service.serviceType}</p>
                    <p><strong>Description:</strong> ${service.description}</p>
                    <p><strong>Price:</strong> $${service.price.toFixed(2)}</p>
                    ${service.discountPercentage ? `<p><strong>Discount:</strong> ${service.discountPercentage}%</p>` : ''}
                </div>
            `;
            
            // Update price field
            if (priceContainer) {
                priceContainer.value = service.price.toFixed(2);
                const discountedPrice = service.discountPercentage ? 
                    service.price * (1 - service.discountPercentage / 100) : 
                    service.price;
                
                const priceLabel = document.querySelector('label[for="price"]');
                if (priceLabel) {
                    priceLabel.innerHTML = `Price: <strong>$${discountedPrice.toFixed(2)}</strong>`;
                }
            }
        })
        .catch(error => {
            console.error('Error fetching service info:', error);
            serviceInfoContainer.innerHTML = '<p class="text-danger">Failed to load service information</p>';
        });
}

/**
 * Initialize appointment action buttons (cancel, complete, etc.)
 */
function initAppointmentActions() {
    // Cancel appointment buttons
    const cancelButtons = document.querySelectorAll('.cancel-appointment');
    cancelButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            if (confirm('Are you sure you want to cancel this appointment? This action cannot be undone.')) {
                const appointmentId = this.dataset.appointmentId;
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = `/appointments/${appointmentId}/cancel`;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });
    
    // Complete appointment buttons
    const completeButtons = document.querySelectorAll('.complete-appointment');
    completeButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            if (confirm('Mark this appointment as completed?')) {
                const appointmentId = this.dataset.appointmentId;
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = `/appointments/${appointmentId}/complete`;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });
}

/**
 * Initialize appointment filtering
 */
function initAppointmentFiltering() {
    const statusFilter = document.getElementById('statusFilter');
    const dateFilter = document.getElementById('dateFilter');
    const searchInput = document.getElementById('appointmentSearch');
    
    if (statusFilter) {
        statusFilter.addEventListener('change', filterAppointments);
    }
    
    if (dateFilter) {
        dateFilter.addEventListener('change', filterAppointments);
    }
    
    if (searchInput) {
        searchInput.addEventListener('input', filterAppointments);
    }
}

/**
 * Filter appointments based on selected criteria
 */
function filterAppointments() {
    const statusFilter = document.getElementById('statusFilter');
    const dateFilter = document.getElementById('dateFilter');
    const searchInput = document.getElementById('appointmentSearch');
    
    const status = statusFilter ? statusFilter.value : null;
    const dateRange = dateFilter ? dateFilter.value : null;
    const searchQuery = searchInput ? searchInput.value.toLowerCase() : '';
    
    const appointments = document.querySelectorAll('.appointment-item');
    
    appointments.forEach(appointment => {
        let showAppointment = true;
        
        // Apply status filter
        if (status && status !== 'ALL') {
            const appointmentStatus = appointment.dataset.status;
            showAppointment = appointmentStatus === status;
        }
        
        // Apply date filter
        if (showAppointment && dateRange) {
            const appointmentDate = new Date(appointment.dataset.date);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            switch (dateRange) {
                case 'TODAY':
                    const todayEnd = new Date(today);
                    todayEnd.setHours(23, 59, 59);
                    showAppointment = appointmentDate >= today && appointmentDate <= todayEnd;
                    break;
                case 'TOMORROW':
                    const tomorrow = new Date(today);
                    tomorrow.setDate(tomorrow.getDate() + 1);
                    const tomorrowEnd = new Date(tomorrow);
                    tomorrowEnd.setHours(23, 59, 59);
                    showAppointment = appointmentDate >= tomorrow && appointmentDate <= tomorrowEnd;
                    break;
                case 'THIS_WEEK':
                    const thisWeekEnd = new Date(today);
                    thisWeekEnd.setDate(thisWeekEnd.getDate() + (7 - thisWeekEnd.getDay()));
                    thisWeekEnd.setHours(23, 59, 59);
                    showAppointment = appointmentDate >= today && appointmentDate <= thisWeekEnd;
                    break;
                case 'UPCOMING':
                    showAppointment = appointmentDate >= today;
                    break;
                case 'PAST':
                    showAppointment = appointmentDate < today;
                    break;
            }
        }
        
        // Apply search filter
        if (showAppointment && searchQuery) {
            const childName = appointment.dataset.childName.toLowerCase();
            const serviceName = appointment.dataset.serviceName.toLowerCase();
            showAppointment = childName.includes(searchQuery) || serviceName.includes(searchQuery);
        }
        
        // Show or hide appointment
        appointment.style.display = showAppointment ? '' : 'none';
    });
    
    // Check if no results
    checkNoResults();
}

/**
 * Check if no results and display message
 */
function checkNoResults() {
    const visibleAppointments = document.querySelectorAll('.appointment-item:not([style*="display: none"])');
    const noResultsMessage = document.getElementById('noAppointmentsMessage');
    
    if (noResultsMessage) {
        if (visibleAppointments.length === 0) {
            noResultsMessage.style.display = 'block';
        } else {
            noResultsMessage.style.display = 'none';
        }
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
