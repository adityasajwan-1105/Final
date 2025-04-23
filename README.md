# TehriMap Application

A web-based mapping application for Tehri with user, admin, and superadmin roles. The application allows users to view location markers, admins to manage markers, and superadmins to manage admin accounts.

## Features

- **User Authentication**: Secure login system with role-based access control
- **Interactive Map**: Built with Leaflet.js for smooth map interactions
- **Location Markers**: Custom emoji markers for different types of locations
- **Role-Based Access**:
  - Users: Can view all markers
  - Admins: Can add/delete their own markers and view all markers
  - Superadmin: Can manage admin accounts and all markers

## Technology Stack

- Backend: Spring Boot
- Frontend: HTML, JavaScript, Tailwind CSS
- Database: MongoDB
- Map: Leaflet.js
- Authentication: JWT (JSON Web Tokens)

## Setup Instructions

1. Clone the repository
2. Configure MongoDB connection in `application.properties`
3. Run the Spring Boot application
4. Access the application at `http://localhost:8080`

## Default Accounts



## Security Notes

- Make sure to change the default superadmin password after first login
- Keep your JWT secret key secure
- Don't commit sensitive information to the repository 