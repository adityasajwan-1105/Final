async function acceptOccurrence(occurrenceId) {
    try {
        const response = await fetch(`/api/occurrences/${occurrenceId}/accept`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('Occurrence accepted successfully');
            // Refresh the page to show updated status
            window.location.reload();
        } else {
            throw new Error('Failed to accept occurrence');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to accept occurrence');
    }
} 