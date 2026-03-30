package com.historicbombs.data;

public enum BombData {
    // === Thermonuclear Weapons ===
    TSAR_BOMBA("tsar_bomba", "Tsar Bomba (AN602)", 50000, "USSR", 1961, BombCategory.THERMONUCLEAR,
        "Largest nuclear weapon ever detonated"),
    TEST_219("test_219", "Test 219", 24200, "USSR", 1962, BombCategory.THERMONUCLEAR,
        "Second largest Soviet test, Novaya Zemlya"),
    TEST_147("test_147", "Test 147", 21100, "USSR", 1962, BombCategory.THERMONUCLEAR,
        "Third largest nuclear test ever"),
    TEST_173("test_173", "Test 173", 19100, "USSR", 1962, BombCategory.THERMONUCLEAR,
        "Fourth largest, Novaya Zemlya"),
    CASTLE_BRAVO("castle_bravo", "Castle Bravo", 15000, "USA", 1954, BombCategory.THERMONUCLEAR,
        "Largest US test, yield 2.5x higher than predicted"),
    CASTLE_YANKEE("castle_yankee", "Castle Yankee", 13500, "USA", 1954, BombCategory.THERMONUCLEAR,
        "Second largest US test, Bikini Atoll"),
    TEST_95("test_95", "Test 95", 12500, "USSR", 1961, BombCategory.THERMONUCLEAR,
        "Part of the massive 1961 test series"),
    CASTLE_ROMEO("castle_romeo", "Castle Romeo", 11000, "USA", 1954, BombCategory.THERMONUCLEAR,
        "First test conducted on a barge"),
    IVY_MIKE("ivy_mike", "Ivy Mike", 10400, "USA", 1952, BombCategory.THERMONUCLEAR,
        "First hydrogen bomb test"),
    TEST_174("test_174", "Test 174", 10000, "USSR", 1962, BombCategory.THERMONUCLEAR,
        "Estimated >10 Mt, exact yield uncertain"),
    HARDTACK_POPLAR("hardtack_poplar", "Hardtack Poplar", 9300, "USA", 1958, BombCategory.THERMONUCLEAR,
        "Operation Hardtack I, Bikini Atoll"),
    HARDTACK_OAK("hardtack_oak", "Hardtack Oak", 8900, "USA", 1958, BombCategory.THERMONUCLEAR,
        "Barge shot at Enewetak"),
    DOMINIC_HOUSATONIC("dominic_housatonic", "Dominic Housatonic", 8300, "USA", 1962, BombCategory.THERMONUCLEAR,
        "Operation Dominic, Christmas Island"),
    CASTLE_UNION("castle_union", "Castle Union", 6900, "USA", 1954, BombCategory.THERMONUCLEAR,
        "Operation Castle series"),
    REDWING_TEWA("redwing_tewa", "Redwing Tewa", 5000, "USA", 1956, BombCategory.THERMONUCLEAR,
        "Operation Redwing, Bikini Atoll"),
    REDWING_NAVAJO("redwing_navajo", "Redwing Navajo", 4500, "USA", 1956, BombCategory.THERMONUCLEAR,
        "Surface burst, Bikini Atoll"),
    REDWING_ZUNI("redwing_zuni", "Redwing Zuni", 3530, "USA", 1956, BombCategory.THERMONUCLEAR,
        "Bikini Atoll"),
    REDWING_CHEROKEE("redwing_cherokee", "Redwing Cherokee", 3800, "USA", 1956, BombCategory.THERMONUCLEAR,
        "First US airdropped thermonuclear test"),
    RDS_37("rds_37", "RDS-37", 3000, "USSR", 1955, BombCategory.THERMONUCLEAR,
        "First true Soviet H-bomb (Teller-Ulam design)"),
    B41("b41", "B41 (Mk-41)", 25000, "USA", 1960, BombCategory.THERMONUCLEAR,
        "Highest yield US weapon ever deployed"),
    CASTLE_NECTAR("castle_nectar", "Castle Nectar", 1690, "USA", 1954, BombCategory.THERMONUCLEAR,
        "Operation Castle"),
    STARFISH_PRIME("starfish_prime", "Starfish Prime", 1450, "USA", 1962, BombCategory.THERMONUCLEAR,
        "High altitude test, massive EMP effect"),
    DOMINIC_SEDAN("dominic_sedan", "Dominic Sedan", 104, "USA", 1962, BombCategory.THERMONUCLEAR,
        "Cratering experiment, 1,280 ft diameter crater"),
    RDS_6S("rds_6s", "RDS-6s (Joe 4)", 400, "USSR", 1953, BombCategory.THERMONUCLEAR,
        "First Soviet thermonuclear test"),
    GRAPPLE_Y("grapple_y", "Grapple Y", 3000, "UK", 1958, BombCategory.THERMONUCLEAR,
        "Largest British nuclear test"),
    CANOPUS("canopus", "Canopus", 2600, "France", 1968, BombCategory.THERMONUCLEAR,
        "First French thermonuclear test"),
    TEST_NO_6("test_no_6", "Test No. 6", 4000, "China", 1976, BombCategory.THERMONUCLEAR,
        "Largest Chinese nuclear test"),
    PUNGGYE_RI("punggye_ri", "Punggye-ri (Test 6)", 250, "North Korea", 2017, BombCategory.THERMONUCLEAR,
        "North Korea's largest test, claimed thermonuclear"),

    // === Fission Weapons ===
    HARDTACK_UMBRELLA("hardtack_umbrella", "Hardtack Umbrella", 8, "USA", 1958, BombCategory.FISSION,
        "Underwater nuclear test"),
    ORANGE_HERALD("orange_herald", "Orange Herald", 720, "UK", 1957, BombCategory.FISSION,
        "Possibly most powerful fission-only bomb ever tested"),
    LITTLE_BOY("little_boy", "Little Boy", 15, "USA", 1945, BombCategory.FISSION,
        "Hiroshima, gun-type uranium bomb"),
    FAT_MAN("fat_man", "Fat Man", 21, "USA", 1945, BombCategory.FISSION,
        "Nagasaki, plutonium implosion bomb"),
    TRINITY("trinity", "Trinity", 19, "USA", 1945, BombCategory.FISSION,
        "First nuclear test ever, Alamogordo NM"),
    RDS_1("rds_1", "RDS-1 (Joe 1)", 22, "USSR", 1949, BombCategory.FISSION,
        "First Soviet nuclear test"),
    SMILING_BUDDHA("smiling_buddha", "Smiling Buddha", 12, "India", 1974, BombCategory.FISSION,
        "India's first nuclear test"),
    CHAGAI_I("chagai_i", "Chagai-I", 40, "Pakistan", 1998, BombCategory.FISSION,
        "Pakistan's first confirmed nuclear test"),
    DAVY_CROCKETT("davy_crockett", "Davy Crockett (W54)", 0.02, "USA", 1962, BombCategory.FISSION,
        "Smallest nuclear weapon ever deployed"),

    // === Thermobaric Weapons ===
    FOAB("foab", "Father of All Bombs (FOAB)", 0.044, "Russia", 2007, BombCategory.THERMOBARIC,
        "Thermobaric weapon, Russia claims 4x MOAB power"),

    // === Conventional Weapons ===
    MOAB("moab", "GBU-43/B MOAB", 0.011, "USA", 2003, BombCategory.CONVENTIONAL,
        "Mother Of All Bombs, 18,700 lbs of H-6 explosive"),
    DAISY_CUTTER("daisy_cutter", "BLU-82 Daisy Cutter", 0.006, "USA", 1970, BombCategory.CONVENTIONAL,
        "15,000 lbs, used in Vietnam to clear jungle for helicopter LZs"),
    GBU_57_MOP("gbu_57_mop", "GBU-57 MOP", 0.003, "USA", 2011, BombCategory.CONVENTIONAL,
        "Massive Ordnance Penetrator, 30,000 lbs bunker buster"),
    GRAND_SLAM("grand_slam", "Grand Slam", 0.0065, "UK", 1945, BombCategory.CONVENTIONAL,
        "22,000 lb earthquake bomb by Barnes Wallis"),
    TALLBOY("tallboy", "Tallboy", 0.0032, "UK", 1944, BombCategory.CONVENTIONAL,
        "12,000 lb earthquake bomb, sank the Tirpitz"),
    T12_CLOUDMAKER("t12_cloudmaker", "T-12 Cloudmaker", 0.0086, "USA", 1948, BombCategory.CONVENTIONAL,
        "44,000 lb demolition bomb, never used in combat"),
    GBU_28("gbu_28", "GBU-28 Bunker Buster", 0.00063, "USA", 1991, BombCategory.CONVENTIONAL,
        "4,700 lbs, laser guided, used in Gulf War"),
    SC_2500_MAX("sc_2500_max", "SC 2500 Max", 0.0017, "Germany", 1940, BombCategory.CONVENTIONAL,
        "5,500 lb Luftwaffe general purpose bomb"),
    MK_84("mk_84", "Mk 84 (JDAM)", 0.000945, "USA", 1965, BombCategory.CONVENTIONAL,
        "Standard 2,000 lb bomb, backbone of US air power"),
    FAB_9000("fab_9000", "FAB-9000", 0.007, "USSR", 1954, BombCategory.CONVENTIONAL,
        "9,000 kg Soviet general purpose bomb"),
    FAB_3000("fab_3000", "FAB-3000", 0.0014, "Russia", 1954, BombCategory.CONVENTIONAL,
        "3,000 kg bomb, recently used in Ukraine conflict"),
    FRITZ_X("fritz_x", "Fritz X", 0.0003, "Germany", 1943, BombCategory.CONVENTIONAL,
        "First precision guided munition in history"),

    // === DO NOT USE Variants (Top 10 by yield, linear scaling) ===
    TSAR_BOMBA_DNU("tsar_bomba_dnu", "Tsar Bomba \u26A0 DO NOT USE \u26A0", 50000, "USSR", 1961, BombCategory.DO_NOT_USE,
        "Largest nuclear weapon ever detonated"),
    TEST_219_DNU("test_219_dnu", "Test 219 \u26A0 DO NOT USE \u26A0", 24200, "USSR", 1962, BombCategory.DO_NOT_USE,
        "Second largest Soviet test, Novaya Zemlya"),
    TEST_147_DNU("test_147_dnu", "Test 147 \u26A0 DO NOT USE \u26A0", 21100, "USSR", 1962, BombCategory.DO_NOT_USE,
        "Third largest nuclear test ever"),
    TEST_173_DNU("test_173_dnu", "Test 173 \u26A0 DO NOT USE \u26A0", 19100, "USSR", 1962, BombCategory.DO_NOT_USE,
        "Fourth largest, Novaya Zemlya"),
    CASTLE_BRAVO_DNU("castle_bravo_dnu", "Castle Bravo \u26A0 DO NOT USE \u26A0", 15000, "USA", 1954, BombCategory.DO_NOT_USE,
        "Largest US test, yield 2.5x higher than predicted"),
    CASTLE_YANKEE_DNU("castle_yankee_dnu", "Castle Yankee \u26A0 DO NOT USE \u26A0", 13500, "USA", 1954, BombCategory.DO_NOT_USE,
        "Second largest US test, Bikini Atoll"),
    TEST_95_DNU("test_95_dnu", "Test 95 \u26A0 DO NOT USE \u26A0", 12500, "USSR", 1961, BombCategory.DO_NOT_USE,
        "Part of the massive 1961 test series"),
    CASTLE_ROMEO_DNU("castle_romeo_dnu", "Castle Romeo \u26A0 DO NOT USE \u26A0", 11000, "USA", 1954, BombCategory.DO_NOT_USE,
        "First test conducted on a barge"),
    IVY_MIKE_DNU("ivy_mike_dnu", "Ivy Mike \u26A0 DO NOT USE \u26A0", 10400, "USA", 1952, BombCategory.DO_NOT_USE,
        "First hydrogen bomb test"),
    TEST_174_DNU("test_174_dnu", "Test 174 \u26A0 DO NOT USE \u26A0", 10000, "USSR", 1962, BombCategory.DO_NOT_USE,
        "Estimated >10 Mt, exact yield uncertain");

    private static final float BASE_POWER = 4.0f;
    private static final float SCALE_FACTOR = 25.0f;
    private static final float MAX_LOG_POWER = 121.5f; // Tsar Bomba's log-scaled power

    private final String registryName;
    private final String displayName;
    private final double yieldKt;
    private final String country;
    private final int year;
    private final BombCategory category;
    private final String description;

    BombData(String registryName, String displayName, double yieldKt, String country,
             int year, BombCategory category, String description) {
        this.registryName = registryName;
        this.displayName = displayName;
        this.yieldKt = yieldKt;
        this.country = country;
        this.year = year;
        this.category = category;
        this.description = description;
    }

    public String getRegistryName() { return registryName; }
    public String getDisplayName() { return displayName; }
    public double getYieldKt() { return yieldKt; }
    public String getCountry() { return country; }
    public int getYear() { return year; }
    public BombCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public boolean isDoNotUse() { return category == BombCategory.DO_NOT_USE; }

    public float getExplosionPower() {
        if (isDoNotUse()) {
            return (float) Math.min(yieldKt / 100.0, 1000.0);
        }
        return (float) (BASE_POWER + SCALE_FACTOR * Math.log10(yieldKt + 1));
    }

    public int getFuseTicks() {
        if (isDoNotUse()) return 240;
        float power = getExplosionPower();
        return (int) (80 + (power / MAX_LOG_POWER) * 160);
    }

    public String getFormattedYield() {
        if (yieldKt >= 1000) {
            double mt = yieldKt / 1000.0;
            if (mt == (int) mt) {
                return String.format("%,.0f kt (%,.0f Mt)", yieldKt, mt);
            }
            return String.format("%,.0f kt (%.1f Mt)", yieldKt, mt);
        }
        if (yieldKt >= 1) {
            return String.format("%,.0f kt", yieldKt);
        }
        if (yieldKt >= 0.001) {
            return String.format("%.3f kt (%.0f tons)", yieldKt, yieldKt * 1000);
        }
        return String.format("%.4f kt (%.0f tons)", yieldKt, yieldKt * 1000);
    }

    /** Get all non-DNU bombs */
    public static BombData[] getStandardBombs() {
        return java.util.Arrays.stream(values())
            .filter(b -> !b.isDoNotUse())
            .toArray(BombData[]::new);
    }

    /** Get only DNU variants */
    public static BombData[] getDnuBombs() {
        return java.util.Arrays.stream(values())
            .filter(BombData::isDoNotUse)
            .toArray(BombData[]::new);
    }
}
