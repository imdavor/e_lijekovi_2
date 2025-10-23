# TODO za e-Lijekovi (Android)

## Verzija
- Trenutna verzija: 1.1.0 (versionCode: 2)
- Verzije i promjene prate se u CHANGELOG.md i VERSION_TRACKER.md

---

## Najnoviji uspjesi (prosinac 2024. – listopad 2025.)
- Napredni JPN Schedule Bar: vizualno praćenje terapijskih segmenata
- Pametno označavanje kartice: boja kartice mijenja se tek kad su svi segmenti uzeti
- Poboljšan kalendar layout: split view, veće točke, bolja preglednost
- Batch Schedule Update: optimiziran performance za grupno uzimanje
- Terapijsko grupiranje po rasporedu (jutro, podne, večer, ostalo)
- "Uzmi sve" funkcionalnost za terapijske grupe
- Intervalno doziranje (antibiotici, fleksibilni intervali, automatsko računanje sljedeće doze)
- Vizualno označavanje uzetih doza po danima
- Ručno sortiranje lijekova unutar grupe (drag & drop)
- Poboljšan UI: kompaktan prikaz, filteri, search bar
- Edit i unos lijekova s podrškom za sve bitne podatke
- Svi glavni problemi iz prethodnih verzija su riješeni (ikonice, import/export, duplikati, serialization, build)

---

## Preostali zadaci / planirane nadogradnje

### 1. Poboljšanja intervalnog doziranja
- [x] Fleksibilan unos početka terapije (datum + vrijeme)
- [x] Automatsko računanje sljedeće doze kroz dane
- [ ] Notifikacije za vrijeme uzimanja
- [ ] Compliance tracking za intervalne lijekove (kasno uzimanje, preskođene doze)

### 2. Compliance i statistike
- [ ] Tjedni/mjesečni pregled adherencije (kalendar s oznakama)
- [ ] Postotak uzimanja po lijekovima (7/30 dana)
- [ ] Trend grafovi i streak counter

### 3. UI i funkcionalna poboljšanja
- [ ] Prikaz cijene lijeka (ako je upisana)
- [ ] Prikaz slike lijeka (opcionalno)
- [ ] Dodatni filteri i sortiranje
- [ ] Drag & Drop reordering funkcionalnost (dodatno testiranje i poboljšanja)
- [ ] Funkcionalnost brisanja lijekova (swipe to delete)
- [ ] Rješavanje grešaka u importu podataka
- [ ] Popravljanje konfliktnih komponenti (npr. MainActivity duplikati)
- [ ] Čišćenje minor upozorenja o nekorištenim parametrima

---

## Napomene
- Ovaj TODO se odnosi isključivo na Android aplikaciju e-Lijekovi.
- Sve glavne funkcionalnosti iz verzije 1.1.0 su stabilne i testirane.
- Za detalje o promjenama pogledati CHANGELOG.md i VERSION_TRACKER.md.
- Sljedeća verzija (1.1.1) bit će fokusirana na bugfixeve i stabilnost.
