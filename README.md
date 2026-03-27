# Hotel Management App

Aplicație Android nativă pentru gestionarea camerelor și rezervărilor unui hotel. Proiectul permite navigarea prin camerele disponibile, căutarea lor după nume sau tip, vizualizarea detaliilor, calculul prețului pentru ședere și administrarea completă a rezervărilor.

## Ce face aplicația

- afișează lista camerelor disponibile;
- permite căutarea camerelor după nume sau tip;
- deschide un ecran de detalii pentru fiecare cameră;
- calculează prețul total al rezervării în funcție de perioadă, număr de oaspeți și mic dejun;
- oferă CRUD complet pentru rezervări: creare, vizualizare, editare și ștergere;
- afișează o secțiune separată pentru rezervările recente;
- poate deschide locația hotelului în aplicația Maps.

## Tehnologii folosite

- Java 17
- Android SDK 34
- Gradle
- AndroidX
- Material Design Components
- View Binding
- SQLite prin `SQLiteOpenHelper`

## Cerințe

- Android Studio instalat
- JDK 17
- Android SDK 34
- un emulator Android sau un dispozitiv fizic cu `minSdk 24` sau mai nou

## Pornire rapidă

1. Clonează sau descarcă proiectul.
2. Deschide folderul în Android Studio.
3. Așteaptă sincronizarea Gradle.
4. Rulează aplicația pe emulator sau pe dispozitiv.

Alternativ, din terminal:

```powershell
.\gradlew assembleDebug
```

APK-ul generat va fi disponibil în:

```text
app\build\outputs\apk\debug\
```

## Funcționalități principale

### 1. Gestionarea camerelor

- lista camerelor este afișată în tab-ul `Rooms`;
- camerele pot fi filtrate prin `SearchView`;
- fiecare cameră are nume, tip, preț pe noapte, descriere și imagine;
- din ecranul de detalii utilizatorul poate porni fluxul de rezervare.

### 2. Gestionarea rezervărilor

- tab-ul `Bookings` afișează rezervările recente și numărul total;
- butonul `Manage Bookings` deschide ecranul complet de administrare;
- din acest ecran se poate crea o rezervare nouă;
- apăsarea pe o rezervare afișează detaliile ei;
- editarea și ștergerea sunt disponibile direct din interfața de management.

### 3. Calculul prețului

Prețul total este calculat pe baza:

- numărului de nopți;
- tipului camerei selectate;
- numărului de oaspeți;
- opțiunii pentru mic dejun.

## Persistența datelor

Aplicația folosește o bază de date SQLite locală:

- baza de date: `hotel_management.db`
- tabela `rooms`
- tabela `bookings`

La prima rulare, aplicația inserează automat 4 camere demonstrative:

- `Ocean Breeze Single`
- `Royal Comfort Double`
- `Azure Executive Suite`
- `Golden Deluxe Retreat`

## Structura proiectului

```text
app/src/main/java/com/example/hotelmanagementapp
├── adapter/        adaptoare RecyclerView pentru camere și rezervări
├── data/           helper pentru SQLite și seed inițial
├── model/          modelele Room și Booking
├── repository/     acces la date pentru rooms și bookings
├── ui/             fragmente și dialoguri UI
├── BookingActivity.java
├── BookingListActivity.java
├── MainActivity.java
└── RoomDetailActivity.java
```

## Ecrane importante

- `MainActivity` gestionează toolbar-ul, căutarea și navigarea între `Rooms` și `Bookings`.
- `RoomsFragment` încarcă și filtrează camerele.
- `RoomDetailActivity` afișează detalii pentru cameră și lansează rezervarea.
- `BookingActivity` creează o rezervare nouă.
- `BookingListActivity` oferă CRUD complet pentru rezervări.
- `EditBookingDialogFragment` permite actualizarea unei rezervări existente.

## Configurație de build

- `compileSdk 34`
- `targetSdk 34`
- `minSdk 24`
- `versionCode 1`
- `versionName 1.0`

## Observații

- proiectul folosește `ViewBinding`, deci accesul la view-uri nu se face prin `findViewById`;
- datele sunt locale și nu există integrare cu backend;
- aplicația este potrivită pentru demonstrații, proiecte academice sau extindere ulterioară cu API, autentificare sau sincronizare cloud.

## Verificare

Build-ul debug poate fi verificat cu:

```powershell
.\gradlew assembleDebug
```
