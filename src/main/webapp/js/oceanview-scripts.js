

document.addEventListener('DOMContentLoaded', function() {
    
    console.log('Ocean View Resort System - UI Scripts Initialized');

    // 1. Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.6s ease';
            setTimeout(() => alert.remove(), 600);
        }, 5000);
    });

    // 2. Active Sidebar link highlighting (Fallback if not handled by JSP)
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar-nav a');
    navLinks.forEach(link => {
        if (currentPath.includes(link.getAttribute('href'))) {
            // link.classList.add('active'); // Usually handled by JSP
        }
    });

    // 3. Global Confirmation for Delete/Deactivate actions
    const deleteButtons = document.querySelectorAll('.btn-danger');
    deleteButtons.forEach(btn => {
        if (btn.tagName === 'BUTTON' && !btn.hasAttribute('onclick')) {
            btn.addEventListener('click', function(e) {
                if (!confirm('Are you sure you want to perform this action? This cannot be undone.')) {
                    e.preventDefault();
                }
            });
        }
    });

    // 4. Handle Table Row Hover Effects
    const tableRows = document.querySelectorAll('.data-table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', () => {
            row.style.cursor = 'pointer';
        });
    });
});

/**
 * Utility: Format currency to LKR
 * @param {number} amount 
 * @returns {string}
 */
function formatLKR(amount) {
    return 'LKR ' + parseFloat(amount).toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

/**
 * Utility: Show a temporary toast notification
 * @param {string} message 
 * @param {string} type (success, error, info)
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.style.position = 'fixed';
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '9999';
    toast.style.boxShadow = '0 4px 15px rgba(0,0,0,0.2)';
    toast.innerHTML = message;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.6s ease';
        setTimeout(() => toast.remove(), 600);
    }, 3000);
}