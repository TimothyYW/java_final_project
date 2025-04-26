public class MenuModel {
    private String kodeMenu;
    private String namaMenu;
    private int hargaMenu;
    private int stokMenu;

    public MenuModel(String kodeMenu, String namaMenu, int hargaMenu, int stokMenu) {
        this.kodeMenu = kodeMenu;
        this.namaMenu = namaMenu;
        this.hargaMenu = hargaMenu;
        this.stokMenu = stokMenu;
    }

    public String getKodeMenu() {
        return kodeMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public int getHargaMenu() {
        return hargaMenu;
    }

    public int getStokMenu() {
        return stokMenu;
    }
}
