Rail – Smart Railway Ticketing System
Objective
A secure, modern, and user-friendly railway ticketing platform using biometrics, QR codes, and camera authentication — designed to reduce fraud and enhance the travel experience.
Target Users
-	Travelers (General Public)
-	Ticket Collectors (TC)
-	Railway Authorities (Admin & Analytics)
Key Features
User Authentication
•	- Login as Traveler or TC
•	- Sign-up with Name, DOB, Email, Password
•	- Role-based dashboard access
Traveler Dashboard
•	- Book Ticket
•	- My Bookings
•	- Cancel Ticket
•	- Journey History
•	- Profile Management
Book Ticket
•	- Input: Source, Destination, Date
•	- Show available trains with number, name, time, and platform
•	- Seat selection (1–6)
•	- Biometric or Selfie authentication
•	- QR Code ticket generation
My Bookings
•	- View upcoming tickets with train info, seat number, QR code
Cancel Ticket
•	- Cancel active bookings with confirmation
Journey History
•	- View past completed trips
•	- Optionally download receipts
 
QR Code Features
•	- QR for station entry/exit and TC validation
•	- Prevents misuse of unconfirmed seats
Security Layer
•	- Biometric and camera authentication
•	- Prevents fake bookings and entry
Profile Section
•	- View Name, DOB
•	- Change password
•	- Logout
Ticket Collector Dashboard
•	- Auto camera scan for QR on login
•	- Shows passenger info: Name, Validity, Seat, Train
BLE and NFC Validation
•	- Fast entry/exit validation
•	- Improves security and prevents duplication
Technical Stack
•	- Frontend: Jetpack Compose (Android Kotlin)
•	- QR Code: ZXing / QRGen
•	- Biometric & Camera: Android BiometricPrompt / CameraX
•	- Train Data: IRCTC API (via RapidAPI) or demo mode
Future Enhancements
•	- Live GPS tracking for trains
•	- E-receipts with fare breakdown
•	- Seat selection map UI
•	- In-app notifications
•	- Admin analytics dashboard
Project Status
Actively under development. Core modules implemented. Looking to expand on NFC validation and live tracking.
