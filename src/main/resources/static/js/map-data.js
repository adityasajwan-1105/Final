// Uttarakhand districts data
const districts = [
    { name: "Tehri Garhwal", lat: 30.3753, lng: 78.4337 },
    { name: "Dehradun", lat: 30.3165, lng: 78.0322 },
    { name: "Haridwar", lat: 29.9457, lng: 78.1642 },
    { name: "Pauri Garhwal", lat: 30.1469, lng: 78.7808 },
    { name: "Rudraprayag", lat: 30.2844, lng: 78.9811 },
    { name: "Chamoli", lat: 30.4227, lng: 79.3286 },
    { name: "Pithoragarh", lat: 29.5828, lng: 80.2181 },
    { name: "Almora", lat: 29.5892, lng: 79.6467 },
    { name: "Nainital", lat: 29.3919, lng: 79.4542 },
    { name: "Udham Singh Nagar", lat: 28.9610, lng: 79.5152 },
    { name: "Champawat", lat: 29.3362, lng: 80.0911 },
    { name: "Bageshwar", lat: 29.8362, lng: 79.7713 },
    { name: "Uttarkashi", lat: 30.7268, lng: 78.4354 }
];

// Marker configurations
const markerIcons = {
    TOURIST_SPOT: '🏛️',
    HOSPITAL: '🏥',
    SCHOOL: '🏫',
    RESTAURANT: '🍽️',
    HOTEL: '🏨',
    POLICE_STATION: '👮',
    CUSTOM: '📍'
};

// Create custom icon for marker
function createCustomIcon(type) {
    return L.divIcon({
        html: `<span style="font-size: 2rem;">${markerIcons[type] || '📍'}</span>`,
        className: 'custom-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 20],
        popupAnchor: [0, -20]
    });
}

// Format date for display
function formatDate(dateString) {
    const options = { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Load markers and occurrences
async function loadMapData(map) {
    const headers = {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
        'Content-Type': 'application/json'
    };

    try {
        // Add district markers with custom styling
        districts.forEach(district => {
            L.marker([district.lat, district.lng], {
                icon: L.divIcon({
                    className: 'custom-marker',
                    html: '<i class="fas fa-map-marker-alt text-indigo-500 text-2xl"></i>',
                    iconSize: [30, 30],
                    iconAnchor: [15, 15]
                })
            })
            .bindPopup(`
                <div class="text-center">
                    <h3 class="font-bold text-lg">${district.name}</h3>
                </div>
            `)
            .addTo(map);
        });

        // Load verified occurrences
        const occurrencesResponse = await fetch('/api/occurrences/verified', { headers });
        if (!occurrencesResponse.ok) {
            throw new Error(`Failed to load occurrences: ${occurrencesResponse.statusText}`);
        }
        const occurrences = await occurrencesResponse.json();
        
        // Process occurrences even if markers fail
        occurrences.forEach(occurrence => {
            if (occurrence.latitude && occurrence.longitude) {
                // Create a highlighted circle area
                const circle = L.circle([occurrence.latitude, occurrence.longitude], {
                    color: '#ef4444',
                    fillColor: '#fee2e2',
                    fillOpacity: 0.5,
                    weight: 2,
                    radius: 1000 // Increased to 1km for better visibility
                }).addTo(map);

                let popupContent = `
                    <div class="occurrence-popup p-3 max-w-sm">
                        <h3 class="font-bold text-lg mb-2">${occurrence.title}</h3>
                        <p class="text-gray-700 mb-3">${occurrence.description}</p>
                        <div class="text-sm text-gray-600 space-y-1">
                            <p><i class="fas fa-user mr-2"></i>Reported by: ${occurrence.reporterName}</p>
                            <p><i class="fas fa-calendar mr-2"></i>Date: ${formatDate(occurrence.reportedAt)}</p>
                            ${occurrence.verifiedBy ? `
                                <p><i class="fas fa-check-circle text-green-500 mr-2"></i>Verified by: ${occurrence.verifiedBy}</p>
                            ` : ''}
                        </div>
                    </div>
                `;

                circle.bindPopup(popupContent);

                // Add hover effect
                circle.on('mouseover', function() {
                    this.setStyle({
                        fillOpacity: 0.7,
                        weight: 3
                    });
                });
                circle.on('mouseout', function() {
                    this.setStyle({
                        fillOpacity: 0.5,
                        weight: 2
                    });
                });
            }
        });

        // Try to load markers separately - if this fails, we still have the occurrences
        try {
            const markersResponse = await fetch('/api/markers', { headers });
            const markers = await markersResponse.json();
            markers.forEach(marker => {
                if (marker.latitude && marker.longitude) {
                    L.marker([marker.latitude, marker.longitude], {
                        icon: createCustomIcon(marker.markerType)
                    })
                    .bindPopup(`
                        <div class="marker-popup p-2">
                            <h3 class="font-bold text-lg mb-2">${marker.name}</h3>
                            <p class="text-gray-700 mb-2">${marker.description}</p>
                            <p class="text-sm text-indigo-600">${marker.markerType.replace('_', ' ')}</p>
                        </div>
                    `)
                    .addTo(map);
                }
            });
        } catch (markerError) {
            console.error('Error loading markers:', markerError);
            // Continue execution even if markers fail
        }

    } catch (error) {
        console.error('Error loading map data:', error);
        throw error;
    }
}

// Export the functions and data
window.districts = districts;
window.loadMapData = loadMapData;
window.createCustomIcon = createCustomIcon; 