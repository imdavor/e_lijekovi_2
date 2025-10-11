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
- **⚠️ Compiler warnings** - Riješeni svi warninzi, kompletno implementiran EditLijekaEkran
- **🔧 Funkcionalnost uređivanja** - Potpuno funkcionalan edit screen s validacijom i spremanjem
- **💾 Automatsko spremanje** - Podaci se automatski spremaju nakon svih promjena
- **⏰ INTERVALNO UZIMANJE** - Dodavanje lijekova s vremenskim intervalima (antibiotici, itd.)

## 📋 Trenutna funkcionalnost
1. **Dodavanje lijeka** - standardno (jutro/popodne/večer) ili intervalno (svakih X sati)
2. **Uređivanje lijeka** - s podrškom za oba tipa uzimanja
3. **Automatska kalkulacija** - vremena uzimanja za intervalne lijekove
4. **Praćenje stanja** - trenutna količina, upozorenja kad treba naručiti
5. **Export/Import** - JSON format za backup podataka
6. **Navigacija** - hamburger menu s 5 sekcija
7. **Validacija** - duplikati, obavezna polja, vremenska ograničenja
8. **Material Design 3** - moderan UI dizajn s novim ikonama

## 🎯 Sljedeći mogući koraci
- [ ] Notifikacije za intervalno uzimanje
- [ ] Prikaz intervalnih lijekova na glavnom ekranu
- [ ] Kalendar s označenim vremenima
- [ ] Push notifikacije za podsjetnik
- [ ] Statistike intervalnog uzimanja

## 📱 Aplikacija: e-LijekoviHR
Naziv aplikacije: `e-LijekoviHR` - Hrvatska aplikacija za praćenje lijekova
