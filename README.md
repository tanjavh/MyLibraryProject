 MyLibraryProject – Spring Boot Microservice Application

Ovaj projekat predstavlja  mikroservisnu Spring Boot aplikaciju, razvijenu u skladu sa zahtevima zadatka.  
Sistem je podeljen na dve nezavisne aplikacije, koje komuniciraju preko REST API-ja.

---

1. Struktura projekta

Projekat se sastoji iz dva Spring Boot modula:

1. onlineLibrary (port 8082)
Frontend aplikacija koja:
- sadrži korisnički interfejs (Thymeleaf)
- ima kompletnu Spring Security konfiguraciju
- upravlja korisnicima, pozajmicama i poslovnom logikom

 Pokreće se na:  
`http://localhost:8082`

---

2. LibraryMicroservice (port 8081)
Backend mikroservis koji:
- služi isključivo kao servis za knjige
- nema frontend
- nema autentifikaciju
- izložen je samo preko REST API-ja

Pokreće se na:  
`http://localhost:8081`

---

 2. Arhitektura (ključna stavka zadatka)

Aplikacija koristi mikroservisnu arhitekturu:

- `onlineLibrary` ➜ klijent
- `LibraryMicroservice` ➜ servis za knjige
- komunikacija se vrši isključivo preko REST-a
- ne postoji direktan pristup bazi između aplikacija

✔ Poštovano razdvajanje odgovornosti (Separation of Concerns)

---

3. Controller-i (zahtev zadatka)

 onlineLibrary
- 5 MVC Controller-a**
- 1 REST Controller**

MVC Controller-i:
- prikaz knjiga (BookController)
- početna(Index) i glavna(Home) stranica(HomeController)
- upravljanje pozajmicama-pozajmljivanje I vraćanje knjiga (LoanController)
- upravljanje ulogama ADMIN I USER (RoleController)
- login,  register, administracija, izmena korisničkog imena(UserController)

REST Controller:
- `/api/users` – komunikacija sa drugim servisima, testovi

---

 LibraryMicroservice
- 1 REST Controller

REST endpoint-i:
- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books`
- `DELETE /api/books/{id}`
- `PUT /api/books/{id}/availability`

✔ Ispunjen zahtev: **GET / POST / DELETE**

---

 4.Spring Security

 onlineLibrary
- Form login (username / password)
- Role:
  - `USER`
  - `ADMIN`
- Zaštićeni endpoint-i
- CSRF zaštita uključena
- Posebna konfiguracija za testove

      LibraryMicroservice
- NEMA Spring Security
- Dostupan samo kao interni REST servis

---

5. Validacija podataka

- Bean Validation (`@NotBlank`, `@Size`, `@Valid`)
- Validacija se radi u DTO sloju
- Greške se prikazuju korisniku (ne puca aplikacija)

---

6. Internacionalizacija (i18n)

Aplikacija podržava dvojezičnost:

- srpski
- engleski

Podržano za:
- statički tekst (HTML)
- validacione poruke
- poruke o greškama

Fajlovi:
- `messages.properties`
- `messages_sr.properties`
- `messages_en.properties`

---

 7. Poslovna pravila

✔ Implementirana sva pravila iz zadatka:

- Maksimalno 3 aktivne pozajmice po korisniku
- Rok za vraćanje knjige:
  - više od 15 dana → upozorenje
  - više od 30 dana → korisnik se blokira
- Blokirani korisnici:
  - ne mogu pozajmljivati nove knjige
- mogu vratiti već pozajmljene knjige

---

    8. Testiranje

- Unit testovi
- Integration testovi
- Poseban `test` profil
- Posebna Security konfiguracija za testove

Testirani su:
- REST endpoint-i
- poslovna logika
- security scenariji
- granični slučajevi (15+ i 30+ dana)

---

 9. Tehnologije

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- Thymeleaf
- REST API
- MariaDB
- H2 (test)
- ModelMapper
- JUnit 5 / Mockito

---

 Pokretanje aplikacije

 1.Pokrenuti LibraryMicroservice

2. Pokrenuti  onlineLibrary

  Zaključak

Ovaj projekat demonstrira:
- pravilnu mikroservisnu arhitekturu
- razdvajanje frontend i backend odgovornosti
- sigurnu autentifikaciju
- REST komunikaciju između servisa
- validaciju i internacionalizaciju
- testiranje Spring Boot aplikacija

Projekat je u potpunosti usklađen sa zahtevima zadatka.
