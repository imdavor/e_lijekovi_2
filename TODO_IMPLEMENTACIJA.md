# TODO - Implementirana funkcionalnost

## ✅ Riješeno
- **Deprecated jvmTarget** - Zamijenjen `kotlinOptions` s `compilerOptions` DSL-om
- **Deprecated statusBarColor** - Koristi se moderan `WindowInsetsControllerCompat` pristup
- **Hamburger menu** - Implementiran s navigacijom između: Početna, Statistike, Postavke, Pomoć, O aplikaciji
- **Jutro/popodne/večer u edit lijeka** - Implementirano u oba ekrana s elegantnim kockama umjesto checkboxova
- **Kontrola duplikata** - Implementirana provjera za postojanje lijeka s istim nazivom (case-insensitive)
- **Material icons** - Svi importi rade ispravno, koriste se Material3 komponente
- **🎨 Kocke za odabir doba dana** - Zamijenjeni checkboxovi s interaktivnim kockama (jutro🌞/popodne🌅/večer🌙)
- **📱 Pokretanje na telefonu** - Popravljeni package nazivi, API nivoi i dodane potrebne permisije

## 📋 Trenutna funkcionalnost
1. **Dodavanje lijeka** - s odabirom više doba dana
2. **Uređivanje lijeka** - s odabirom više doba dana  
3. **Praćenje stanja** - trenutna količina, upozorenja kad treba naručiti
4. **Export/Import** - JSON format za backup podataka
5. **Navigacija** - hamburger menu s 5 sekcija
6. **Validacija** - duplikati, obavezna polja
7. **Material Design 3** - moderan UI dizajn

## 🎯 Mogući poboljšanja
- [ ] Dodavanje slika za lijekove
- [ ] Notifikacije za uzimanje lijekova
- [ ] Kalendar praćenja
- [ ] Statistike uzimanja
- [ ] QR kod za brže dodavanje
- [ ] Sinkronizacija s cloudom

## 📱 Aplikacija: e-LijekoviHR
Naziv aplikacije: `e-LijekoviHR` - Hrvatska aplikacija za praćenje lijekova
