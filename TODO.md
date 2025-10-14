# TODO za e-Lijekovi

# e-lijekovi - TODO Lista za Razvoj

## 📦 VERZIONIRANJE SISTEM
**Trenutna verzija**: 1.1.0 (versionCode: 2)
**Sledeća verzija**: 1.1.1 (PATCH - bug fixes)

📋 **Tracking fajlovi**:
- `CHANGELOG.md` - Kompletna istorija svih verzija
- `VERSION_TRACKER.md` - Aktivno praćenje izmena u toku
- `build.gradle.kts` - Automatsko ažuriranje versionCode/versionName

🔄 **Workflow**:
1. Sve izmene se dodaju u VERSION_TRACKER.md
2. Na release se prebacuju u CHANGELOG.md  
3. Verzija se ažurira u build.gradle.kts
4. Git tag se kreira za verziju

---

## ✅ NAJNOVIJI USPJESI (Listopad 2025)

### 🎯 **NAJNOVIJI VELIKI UPDATE - Schedule Tracking Sistem** (9.10.2025)
- ✅ **Napredni JPN Schedule Bar** - Vizualno praćenje terapijskih segmenata:
    - 🔴 **Crveno** - lijek predviđen za to vrijeme ali nije uzet
    - 🟢 **Zeleno** - lijek uzet za to vrijeme
    - ⚪ **Svijetlo** - lijek nije predviđen za to vrijeme
    - 📊 Svaki lijek može imati kombinacije (J+N, J+P+N, itd.)
- ✅ **Pametno označavanje kartice** - Boja kartice mijenja se tek kad su SVI schedule segmenti uzeti
    - Lijek u jutarnjoj grupi: označava se "J" ali kartica ostaje bijela
    - Isti lijek u večernjoj grupi: vidi označen "J", čeka "N"
    - Tek kad su uzeti SVI potrebni segmenti, kartica postaje zelena
- ✅ **Poboljšan kalendar layout** - Split view dizajn:
    - 📅 **Lijevo**: Kompaktni kalendar (40% širine) s većim i uočljivijim točkama
    - 📋 **Desno**: Lista uzimanih lijekova (60% širine)
    - 🎯 Veće točke (8px/10px) s bijelim obrubom i sjenama za bolje označavanje
- ✅ **Batch Schedule Update** - Optimiziran performance za grupno uzimanje:
    - Jedan setState poziv umjesto petlje
    - Sprječava race conditions u React state
    - Pravilno zapisuje schedule segmente u kalendar

### 📊 **Prethodne funkcionalnosti**
- ✅ **Terapijsko grupiranje po rasporedu** - Lijekovi grupirani po vremenu uzimanja:
    - 🌅 **Jutro** - lijekovi označeni za jutarnje uzimanje
    - ☀️ **Popodne** - lijekovi označeni za popodnevno uzimanje
    - 🌙 **Navečer** - lijekovi označeni za večernje uzimanje
    - 📋 **Ostalo** - lijekovi bez definiranog rasporea
- ✅ **"Uzmi sve" funkcionalnost** - Grupno uzimanje doze za cijelu terapijsku grupu
    - Gumb u zaglavlju svake grupe: "Uzmi sve (3)"
    - Automatsko ažuriranje količina za sve lijekove u grupi
    - Pravilno označavanje schedule segmenata
- ✅ **Intervalsko doziranje za antibiotike** - Podržava lijekove s vremenskim intervalima
    - ⏰ Prikaz intervala: "Svaki 8h", "Svaki 12h"
    - 🕐 Poštuje "Počni u:" vrijeme (npr. 09:00, 17:00, 01:00)
    - ⏱️ **Sljedeće vrijeme** - automatski računa sljedću dozu
    - 📅 **Praviljan broj dana** - ispravno računanje trajanja terapije
    - 🔧 Kompatibilnost s postojećim formatima podataka
- ✅ **UI optimizacija** - Smanjeni razmaci i poboljšana efikasnost prostora
    - Header padding smanjen za 60%
    - Footer potpuno uklonjen
    - Lista spacing optimiziran za 80% manje prostora
    - Compact search bar s filter gumbovima
- ✅ **Poboljšan edit sistema** - Ispravke u edit funkcionalnosti
    - Popravljen prop passing između komponenti
    - Edit modal se otvara s podacima postojećeg lijeka
    - Kompatibilnost naziva polja (intervalHours vs intervalDosing.hours)
- ✅ **Dnevno praćenje uzimanja doze** - Vizualno označavanje uzete doze za svaki dan
    - ✅/☐ Checkbox za označavanje uzete doze današnji dan
    - Zeleni indikator kada je doza uzeta
    - Automatsko spremanje stanja u AsyncStorage
    - Per-lijek tracking s datumskim ključevima
- ✅ **Drag & Drop sortiranje** - Ručno prebacivanje redoslijeda kartica lijekova
    - Long press za aktivaciju drag moda
    - Vizualni shadow efekt tijekom drag operacije
    - Automatsko spremanje novog redoslijeda
    - Funkcionira u "Lista" prikazu (ne u grupiranom)

## 🔄 SLJEDEĆI KORAK - Poboljšanja intervalskog doziranja

### 🚀 **PRIORITET 1 - Antibiotička terapija optimizacija**
- [ ] **Napredniji interval sistem** - Poboljšanja za precizno antibiotičko doziranje:
    - ⏰ **Fleksibilni start times** - Korisnik definira kad je počeo terapiju
    - 🔔 **Pametne notifikacije** - Točno na vrijeme intervala (8h, 12h, 24h)
    - 📊 **Compliance tracking** - Praćenje odstupanja od schedule
    - 🕐 **"Kasno uzimanje"** - Označavanje doze uzete izvan prozora
- [ ] **Poboljšano računanje intervala** - Preciznost do minute:
    - 📅 Start date + time kombinacija
    - ⏳ Računanje sljedećeg intervala kroz dane
    - 🔄 Automatska prilagodba za preskok doze
- [ ] **Interval compliance izvještaji** - Detaljno praćenje adherencije:
    - 📈 Graf vremena uzimanja vs planiranog vremena
    - ⚠️ Upozorenja za česte kašnjenja
    - 📊 Statistike efikasnosti terapije

### 📊 **PRIORITET 2 - Compliance praćenje i statistike**
- [ ] **Tjedni/mjesečni compliance pregled** - Detaljno praćenje adherencije
    - Kalendar prikaz s ✅/❌ oznakama za svaki dan
    - Postotak uzimanja po lijekovima (7 dana, 30 dana)
    - Trend grafovi adherencije po tjednima
    - "Streak" counter za uzastopne dane bez propusta
- [ ] **Pametne notifikacije** - Automatski podsjetnici na bazi terapijskih grupa i compliance
    - Push notifikacije za jutarnju/popodnevnu/večernju terapiju
    - Intervalski podsjetnici za antibiotike (svaki 8h/12h)
    - Adaptivni podsjetnici (više notifikacija za nižu adherenciju)
    - "Snooze" funkcionalnost za odgađanje doze
- [ ] **Napredni drag & drop** - Proširena sortiranje funkcionalnost
    - Drag & drop i u grupiranom prikazu (unutar grupa)
    - Prebacivanje lijekova između terapijskih grupa drag-om
    - Vizualni drop zones za različite grupe
    - Undo/Redo funkcionalnost za sortiranje

## 🎯 Prioritet 1 - Kritične funkcionalnosti

### 🔒 Sigurnost i Stabilnost
- [x] **Popravi JSON persistenciju** - ✅ RIJEŠENO! Podaci se### 🔧 Trenutni problemi za rješavanje:
- ✅ JSON fajl se sada sprema ispravno na disk - RIJEŠENO!
- ✅ **KRITIČNO**: 8 različitih App.js datoteka stvaraju konfuziju - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Memory leaks u notification listenerima - ✅ RIJEŠENO!
- ❌ **KRITIČNO**: Hardkodirani API_BASE_URL u više datoteka
- ❌ **KRITIČNO**: Nema error handling-a za network requests
- ❌ **VAŽNO**: Loading states za sve API pozive
- ❌ Expo Go ograničenja za push notifikacije
- ❌ Nedostaje input validacija**VAŽNO**: Memory leaks u notification listenerima -
- ✅ RIJEŠENO!KRITIČNO**: 8 različitih App.js datoteka stvaraju konfuziju - ✅ RIJEŠENO!
- ✗ **KRITIČNO**: Hardkodirani API_BASE_URL u više datoteka
- ✅ **KRITIČNO**: Nema error handling-a za network requests - ✅ RIJEŠENO!da čuvaju u data/medications.json
- [ ] **Frontend input validation** - Provjera naziva lijeka (min 2 znaka, max 50), broj tableta (>0, <9999)
- [ ] **Backend data validation** - Pydantic modeli s detaljnom validacijom
- [ ] **Network error handling** - Retry logika, timeout handling, offline detection
- [x] **Data backup sistem** - ✅ IMPLEMENTIRANO! Export/import lijekova u JSON format + "Obriši sve podatke"
- [ ] **Crash reporting** - Sentry ili slična služba za praćenje grešaka
- [ ] **API rate limiting** - Zaštita od spam zahtjeva
- [ ] **Offline mode** - Omogući rad aplikacije bez interneta s sync kada se vrati konekcija

### 📱 Korisničko iskustvo
- [x] **Main Menu/Navigation** - ✅ IMPLEMENTIRANO! Hamburger menu sa drawer navigacijom
    - [x] Settings screen (notifikacije, jezik, backup/restore) - ✅ GOTOVO!
    - [x] About screen (verzija, kontakt, licenca) - ✅ GOTOVO!
    - [ ] Help screen (FAQ, tutorial, podrška) - može se dodati u buduće verzije
    - [x] Statistics screen (ukupno lijekova, compliance rate) - ✅ GOTOVO!
    - [ ] Profile screen (korisničke informacije) - nije prioritet za sada
- [ ] **Loading indikatori** - Spinners za API pozive, skeleton screens, progress bars
- [ ] **Error messages** - User-friendly poruke grešaka s retry opcijama
- [ ] **Success feedback** - Toast notifikacije za uspješne akcije
- [x] **Potvrda brisanja** - ✅ IMPLEMENTIRANO! "Želite li obrisati lijek?" dialog
- [ ] **Pull-to-refresh** - povuci prema dolje za osvježavanje liste lijekova
- [x] **Swipe to delete** - ✅ IMPLEMENTIRANO! Povuci lijevo za brisanje lijeka
- [x] **Search/Filter funkcionalnost** - ✅ IMPLEMENTIRANO! MedicineFilters komponenta s pretraživanjem i filterima
- [x] **Bulk operations** - ✅ IMPLEMENTIRANO! Multi-select označavanje i brisanje više lijekova odjednom
- [x] **Therapy grouping** - ✅ IMPLEMENTIRANO! Grupiranje lijekova po vremenu uzimanja (jutarnja/popodnevna/večernja/intervalska terapija)
- [x] **Interval dosing** - ✅ IMPLEMENTIRANO! Podržava lijekove s vremenskim intervalima (npr. antibiotici svakih 8 sati)
- [x] **Daily therapy tracking** - ✅ IMPLEMENTIRANO! Označavanje da je terapija uzeta za taj dan s vizualnim indikatorima
- [x] **Group dose taking** - ✅ IMPLEMENTIRANO! "Uzmi sve" funkcionalnost za cijelu terapijsku grupu
- [x] **UI repositioning** - ✅ IMPLEMENTIRANO! Select checkbox premješten na lijevu stranu, search/data/add na desno
- [ ] **Undo functionality** - poništi zadnju akciju (brisanje, editiranje)

## 🎯 Prioritet 2 - Poboljšanja UX/UI

### 🎨 Dizajn
- [ ] **Hrvatski theme** - boje inspirisane hrvatskim zdravstvom
- [ ] **Dark mode** - noćni način rada
- [ ] **Animacije** - smooth prijelazi između ekrana
- [ ] **Ikone lijekova** - vizualni prikaz različitih tipova lijekova
- [ ] **Status badge** - vizualni indikatori za reorder, low stock, itd.

### 🔔 Napredne Notifikacije
- [ ] **Smart scheduling algoritam** - optimiziraj vrijeme podsjetnika na osnovu korisničkih navika
- [ ] **Snooze funkcionalnost** - odgodi podsjetnik za 5/15/30 min
- [ ] **Adaptive notifications** - promijeni frekvenciju na osnovu compliance rate-a
- [ ] **Contextual reminders** - "Uzmi lijek prije jela" s podsjetnicima o hrani
- [ ] **Weekly/Monthly summary** - detaljni izvještaji o uzimanju lijekova
- [ ] **Customize sounds/vibrations** - različiti zvukovi za različite lijekove
- [ ] **Geofencing reminders** - podsjeti kad si u blizini ljekarne
- [ ] **Weather-based reminders** - prilagodi notifikacije na osnovu vremena (astma, artritis)
- [ ] **Family notifications** - obavijesti članova obitelji o compliance-u

## 🎯 Prioritet 3 - Napredne funkcionalnosti

### ⚕️ Medicinske integracije
- [ ] **s:doktor API integracija** - direktna veza sa hrvatskim zdravstvenim sustavom
- [ ] **Recept skeniranje** - skeniraj recept kamerom i automatski dodaj lijekove
- [ ] **Drug interactions** - upozorenja o mogućim interakcijama lijekova
- [ ] **Side effects tracking** - praćenje nuspojava
- [ ] **Doctor's notes** - bilješke liječnika uz lijekove

### 📊 Analytics i izvještaji
- [ ] **Compliance tracking** - postotak uzimanja lijekova
- [ ] **Health trends** - grafikoni i trendovi
- [ ] **Export reports** - PDF izvještaji za liječnika
- [ ] **Family sharing** - dijeljenje između članova obitelji
- [ ] **Caregiver mode** - način rada za njegovatelje

### 🏥 Hrvatska specifika
- [ ] **HZZO integracija** - povezivanje sa hrvatskim zdravstvenim osiguranjem
- [ ] **Ljekarne mapa** - pronađi najbližu ljekarnu na mapi
- [ ] **Cijena praćenje** - usporedi cijene lijekova u ljekarnama
- [ ] **Dozvola putovanja** - generiraj dokumente za putovanje sa lijekovima
- [ ] **Hitna pomoć** - brz pristup hitnim brojevima (194, 112, 01 4440 278)

## 🎯 Prioritet 4 - Tehnička poboljšanja

### 🧪 Testing i Quality Assurance
- [ ] **Unit tests** - Testiranje med_manager.py funkcionalnosti
- [ ] **Integration tests** - API endpoint testiranje
- [ ] **Mobile app testing** - React Native Testing Library
- [ ] **E2E testing** - Detox ili Maestro za end-to-end testove
- [ ] **Performance testing** - Load testing API-ja, memory profiling mobile app
- [ ] **Accessibility testing** - Screen reader podrška, high contrast mode
- [ ] **Device testing** - Testiranje na različitim Android/iOS uređajima

### 🛠 Backend
- [ ] **PostgreSQL migracija** - prebaciti sa JSON na pravu bazu
- [ ] **Database migrations** - Alembic za schema changes
- [ ] **API versioning** - podrška za različite verzije API-ja (/v1/, /v2/)
- [ ] **Rate limiting** - ograniči broj zahtjeva po korisniku (slowapi)
- [ ] **Caching** - implementiraj Redis cache za često korištene podatke
- [ ] **Background jobs** - Celery queue za pozadinske zadatke (backup, notifikacije)
- [ ] **API dokumentacija** - Poboljšaj Swagger/OpenAPI docs s primjerima
- [ ] **Health checks** - Detaljni /health endpoint s database status
- [ ] **Metrics collection** - Prometheus/Grafana za monitoring

### 📱 Mobile
- [ ] **Navigation cleanup** - Konsolidirati navigation logiku, ukloniti duplikate
- [ ] **State management** - Context API ili Zustand za globalno stanje
- [ ] **Push notifications** - Firebase/OneSignal za pravi push (ne lokalne notifikacije)
- [ ] **Biometric authentication** - Touch ID/Face ID za pristup aplikaciji
- [ ] **Auto-update** - Expo Updates ili CodePush za brže updateove
- [ ] **Performance optimization** - React.memo, useMemo, lazy loading
- [ ] **Bundle optimization** - Analizirati i smanjiti bundle size
- [ ] **Image optimization** - WebP format, lazy loading slika
- [ ] **Deep linking** - Podrška za URL scheme (med://open-medication/123)
- [ ] **App shortcuts** - iOS/Android app shortcuts za brze akcije

### 🔐 Sigurnost
- [ ] **End-to-end encryption** - enkriptiraj osjetljive podatke
- [ ] **OAuth2 login** - Google/Apple/Facebook login
- [ ] **GDPR compliance** - usklađenost sa europskim propisima
- [ ] **Audit logging** - logiranje svih akcija korisnika
- [ ] **Security headers** - dodaj sigurnosne zaglavlja u API

## 🎯 Prioritet 5 - Proširenja

### 🌍 Internacionalizacija
- [ ] **Multi-language support** - engleski, njemački, talijanski
- [ ] **Currency support** - različite valute za cijene lijekova
- [ ] **Timezone handling** - podrška za različite vremenske zone
- [ ] **Regional health systems** - adaptacija za druge zemlje

### 🤖 AI/ML features
- [ ] **Smart dosage suggestions** - AI preporuke za doziranje
- [ ] **OCR recept scanning** - automatsko čitanje recepta
- [ ] **Predictive analytics** - predviđanje kada će lijekovi biti potrebni
- [ ] **Chatbot support** - AI asistent za pitanja o lijekovima
- [ ] **Voice commands** - dodaj lijek glasovnom komandom

### 📈 Business features
- [ ] **Premium subscription** - napredniji features za pretplatnike
- [ ] **Pharmacy partnerships** - partnerstvo sa ljekarnama
- [ ] **Insurance integration** - integracija sa osiguranjem
- [ ] **Telemedicine** - video pozivi sa liječnicima
- [ ] **Medication delivery** - dostava lijekova na kućnu adresu

## 🎯 Prioritet 0 - KRITIČNO (Blokeri za produkciju)

### 🚨 Sigurnost i Stabilnost
- [x] **Code cleanup** - ✅ ZAVRŠENO! Uklonjeno 6 duplikatnih App datoteka u archive/ folder
- [x] **Error boundaries** - ✅ ZAVRŠENO! Implementiran ErrorBoundary komponenta s user-friendly UI
- [x] **API error handling** - ✅ ZAVRŠENO! Dodani try/catch blokovi za sve API pozive s user-friendly porukama
- [ ] **Input sanitization** - Validirati sve korisničke inpute (naziv lijeka, broj tableta)
- [ ] **Environment configuration** - Premjestiti hardkodirane IP adrese u config datoteke
- [x] **Memory leaks** - ✅ RIJEŠENO! Notification listeners cleanup u useEffect ispravljen
- [ ] **API security** - Dodati basic authentication ili API ključeve

### 🔧 Tehnički dugovi
- [ ] **Single App.js** - Konsolidirati sve App verzije u jednu glavnu datoteku
- [ ] **Proper navigation** - Odabrati između hamburger menu ili drawer navigacije
- [ ] **Consistent API calls** - Standardizirati API endpoint konfiguraciju
- [ ] **Loading states** - Dodati loading indikatore za sve async operacije
- [ ] **Offline handling** - Implementirati basic offline mode
- [ ] **Data validation** - Backend Pydantic validacija za sve endpoints

---

## 📝 Bilješke o trenutnom stanju

### ✅ **Implementirano (01.10.2025):**
- ✅ Potpuna CRUD funkcionalnost za lijekove (Create, Read, Update, Delete)
- ✅ React Native mobile aplikacija sa Expo
- ✅ Python FastAPI backend
- ✅ Lokalne notifikacije
- ✅ Hrvatska lokalizacija
- ✅ Potvrda uzimanja doze
- ✅ Status tracking (remaining tablets, reorder threshold)
- ✅ 7-dnevna pravila za ponovnu narudžbu (hrvatski standard)
- ✅ Floating Action Button (FAB) za dodavanje lijekova
- ✅ Modal forms za dodavanje i uređivanje lijekova
- ✅ Main Menu/Navigation sa hamburger menu
    - ✅ React Navigation Drawer implementiran
    - ✅ Settings screen sa potpunim postavkama
    - ✅ Statistics screen sa detaljnim statistikama i grafikonima
    - ✅ About screen sa informacijama o aplikaciji
    - ✅ Custom drawer design sa hrvatskim brendiranjem
- ✅ **NOVO: Multi-select bulk operations**
    - ✅ Označavanje više kartica odjednom
    - ✅ Grupno brisanje označenih lijekova
    - ✅ "Odaberi sve" i "Poništi sve" funkcionalnosti
    - ✅ Smart potvrda s prikazom imena lijekova
- ✅ **NOVO: Data management poboljšanja**
    - ✅ Export/Import JSON podataka (web compatible)
    - ✅ "Obriši sve podatke" funkcionalnost
    - ✅ Web-friendly confirmations i file downloads
- ✅ **NOVO: Tehnička poboljšanja**
    - ✅ Notification listeners cleanup (memory leak fix)
    - ✅ Web platform compatibility improvements
    - ✅ Cross-platform file handling (web + mobile)

### 🔧 **Trenutni problemi za rješavanje:**
- ✅ JSON fajl se sada sprema ispravno na disk - RIJEŠENO!
- ✅ **KRITIČNO**: 8 različitih App.js datoteka stvaraju konfuziju - ✅ RIJEŠENO!
- ✅ **KRITIČNO**: Memory leaks u notification listenerima - ✅ RIJEŠENO!
- ✅ **KRITIČNO**: Edit funkcionalnost ne radi - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Intervalski lijekovi pokazuju 0 dana - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Sljedeće vrijeme se ne prikazuje - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Vrijeme uzimanja pogrešno računato - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Nedostaje vizualno označavanje uzete doze - ✅ RIJEŠENO!
- ✅ **VAŽNO**: Nema drag & drop sortiranja - ✅ RIJEŠENO!
- ❌ **KRITIČNO**: Hardkodirani API_BASE_URL u više datoteka
- ❌ **VAŽNO**: Loading states za sve API pozive
- ❌ **VAŽNO**: Nedostaju push notifikacije za intervalske lijekove
- ❌ **VAŽNO**: Compliance statistike i trend grafovi
- ❌ Expo Go ograničenja za push notifikacije
- ❌ Nedostaje input validacija

### 🚀 **Sljedeći koraci (po prioritetu):**
1. ✅ **[KRITIČNO]** Cleanup - Obrisati nepotrebne App datoteke i konsolidirati kod
2. ✅ **[KRITIČNO]** Error handling - Dodati try/catch za sve API pozive
3. ✅ **[VAŽNO]** Memory management - Cleanup notification listeners
4. ✅ **[VAŽNO]** Multi-select operations - Bulk delete functionality
5. **[KRITIČNO]** Configuration - Premjestiti API URL u environment config
6. **[VAŽNO]** Input validation - Validacija na frontend i backend strani
7. **[VAŽNO]** Loading states - UI indikatori za sve async operacije
8. **[NORMALNO]** Testing - Unit i integration testovi
9. **[NORMALNO]** Documentation - Poboljšati README datoteke
10. **[BUDUĆE]** Production deployment - Build i distribucija

---

##   **Detaljni tehnički zadaci (za developere):**

### Code Quality
- [ ] **ESLint + Prettier** - Automatsko formatiranje koda
- [ ] **TypeScript migration** - Postupno prebacivanje na TypeScript
- [ ] **Component documentation** - Storybook za React Native komponente
- [ ] **API documentation** - OpenAPI 3.0 s detaljnim primjerima
- [ ] **Git hooks** - Pre-commit linting, automated testing
- [ ] **Continuous Integration** - GitHub Actions za automated building/testing

### DevOps i Deployment
- [ ] **Docker containers** - Kontejnerizacija backend aplikacije
- [ ] **CI/CD pipeline** - Automated deployment na staging/production
- [ ] **Environment management** - Development/Staging/Production konfiguracije
- [ ] **Monitoring setup** - Logging, error tracking, performance monitoring
- [ ] **Backup strategy** - Automated database backups
- [ ] **Load balancing** - Nginx setup za production

##  💡 **Ideje za buduće verzije:**

### Emerging Technologies
- **Smart watches integracija** - podrška za Apple Watch/Wear OS s native complications
- **IoT povezivanje** - smart pill dispensers, bluetooth lijek bočice
- **Blockchain** - nepromjenjivi medicinski zapisi na Ethereum/Polygon
- **AR features** - proširena stvarnost za prepoznavanje lijekova kamerom
- **Machine learning** - personalizirane preporuke na osnovu povijesti uzimanja
- **Voice AI** - integracija s Siri/Google Assistant za voice commands
- **Wearable sensors** - integracija s fitness trackerima za health metrics

### Advanced Healthcare Features
- **Genetic testing integration** - personalizirano doziranje na osnovu genetike
- **Clinical trial matching** - pronalaženje relevantnih kliničkih studija
- **Real-world evidence** - prikupljanje anonymiziranih podataka za istraživanje
- **Predictive analytics** - ML modeli za predviđanje compliance-a
- **Social features** - support grupe, peer motivation

---
*Zadnje ažuriranje: 6. listopada 2025.*
*Aplikacija je u fazi beta testiranja - uspješno implementirano terapijsko grupiranje i intervalsko doziranje.*
*Fokus na notifikacije i automatizaciju u sljedećoj fazi razvoja.*

## 🎉 **Najnovije implementirane funkcionalnosti (06.10.2025):**

### 🏥 **Terapijsko grupiranje po rasporedu uzimanja**
- ✅ **Grupiranje po terapiji** - Lijekovi grupirani po vremenu: 🌅 Jutro, ☀️ Popodne, 🌙 Navečer, 📋 Ostalo
- ✅ **"Uzmi sve" gumbovi** - Grupno uzimanje svih lijekova iz terapijske grupe jednim klikom
- ✅ **Prebacivanje prikaza** - Toggle između "Lista" i "Po rasporedu" načina prikaza
- ✅ **Automatsko grupiranje** - Ako lijekovi imaju raspored, automatski se aktivira grupiranje

### 💊 **Poboljšano intervalsko doziranje za antibiotike**
- ✅ **Ispravno računanje dana** - Podržava i `intervalHours` i `intervalDosing.hours` formate
- ✅ **Prikaz intervala** - "⏰ Svaki 8h" umjesto starih informacija o rasporedu
- ✅ **Sljedeće vrijeme uzimanja** - "⏱️ Sljedeći: 16:00" na bazi "Počni u:" postavke
- ✅ **Pametni algoritam** - Prirodni intervali (08:00, 16:00, 00:00) umjesto nasumičnih vremena
- ✅ **Poštovanje startTime** - Uzima u obzir "Počni u: 09:00" za računanje sljedećih doza
- ✅ **Debug podrška** - Console log informacije za troubleshooting intervalskih lijekova
- ✅ **Kompatibilnost podataka** - Dodana podrška za stare i nove formate spremanja

### 🎨 **UX optimizacija i čišći dizajn**
- ✅ **Smanjeni spacing** - Header, footer i lista optimizirani za 60-80% manje prostora
- ✅ **Kompaktni search bar** - [Select][Search][Filter] u jednom redu
- ✅ **Uklonjen dupli sadržaj** - Uklonjene redundantne količine i filter gumbovi
- ✅ **Popravljeni edit** - Modal se otvara s ispravnim podacima postojećeg lijeka
- ✅ **Cross-platform kompatibilnost** - Radi jednako na web browseru i Simple Browser-u

### 🔧 **Tehnička poboljšanja**
- ✅ **Data format standardizacija** - Usklađeni nazivi polja između komponenti
- ✅ **Backward compatibility** - Podržava stare formate podataka iz prethodnih verzija
- ✅ **Error handling** - Dodani try/catch blokovi s debug informacijama
- ✅ **Performance optimization** - SectionList umjesto FlatList za bolje performanse s grupiranjem

## 🎉 **Prethodne implementirane funkcionalnosti (01.10.2025):**

### ✨ Multi-select bulk operations
- **Multi-select mode** - Toggle gumb ☐/✓ u header-u za aktivaciju
- **Checkbox selection** - Označavanje kartica klikom ili checkbox-om
- **Bulk delete** - Grupno brisanje označenih lijekova s pametnom potvrdom
- **Toolbar akcije** - "Sve", "Niš", "🗑️ (broj)" za brže označavanje
- **Visual feedback** - Označene kartice s plavim okvirom i pozadinom
- **Smart disable** - Edit/Delete/TakeDose gumbovi sakriveni u multi-select modu

### 💾 Data management poboljšanja
- **Cross-platform export** - Web-compatible JSON download preko Blob API-ja
- **Cross-platform import** - HTML5 file picker + FileReader za web kompatibilnost
- **Clear all data fix** - Ispravljena "Obriši sve podatke" funkcionalnost
- **Web confirmations** - window.confirm umjesto Alert.alert za web platformu
- **Smart test data** - Test lijek se dodaje samo pri prvom pokretanju

### 🔧 Tehnička poboljšanja
- **Memory leak fix** - Ispravljen notification listeners cleanup s .remove() metodom
- **Web compatibility** - Improved cross-platform file handling i UI components
- **Error boundary** - Bolje error handling s detaljnim stack trace logovanjem
- **Platform detection** - Smart behavior na osnovu platforme (web vs mobile)
