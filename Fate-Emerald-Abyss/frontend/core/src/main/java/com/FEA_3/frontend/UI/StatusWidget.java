package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class StatusWidget extends Table implements UnitObserver {
    private Label valueLabel;
    private Image barImage;
    private Image bgBarImage; // Background abu-abu di belakang bar
    private float maxBarWidth = 180f; // Lebar maksimal bar dalam pixel
    private boolean isMp; // Penanda apakah ini HP atau MP

    public StatusWidget(Skin skin, Color barColor, boolean isMp) {
        this.isMp = isMp;

        // 1. Label Nilai (Contoh: "HP: 1000/1000")
        valueLabel = new Label(isMp ? "MP: --" : "HP: --", skin);
        valueLabel.setFontScale(0.8f);

        // 2. Texture Bar (Warna Solid)
        Texture barTexture = createSolidTexture(barColor);
        barImage = new Image(barTexture);

        // 3. Texture Background Bar (Abu-abu gelap)
        Texture bgTexture = createSolidTexture(Color.DARK_GRAY);
        bgBarImage = new Image(bgTexture);

        // --- SUSUN LAYOUT ---
        // Baris 1: Teks Angka
        this.add(valueLabel).left().padBottom(2).row();

        // Baris 2: Bar Container (Tumpuk Background dan Bar Utama)
        Table barContainer = new Table();

        // Kita gunakan Stack atau trik layout agar background pas di belakang
        // Cara termudah di Table: Set background table jadi abu-abu
        barContainer.setBackground(new TextureRegionDrawable(bgTexture));

        // Masukkan Bar Utama (Foreground)
        barContainer.add(barImage).height(15).width(maxBarWidth).left();

        this.add(barContainer).left().height(15).width(maxBarWidth);
    }

    // Helper membuat texture warna solid
    private Texture createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    // Dipanggil otomatis oleh Observer (HP)
    @Override
    public void onHealthChanged(int currentHp, int maxHp) {
        if (!isMp) {
            updateDisplay(currentHp, maxHp);
        }
    }

    @Override
    public void onDamageTaken(int amount, boolean isCrit) {

    }

    // Dipanggil manual (MP)
    public void updateMp(int currentMp, int maxMp) {
        if (isMp) {
            updateDisplay(currentMp, maxMp);
        }
    }

    private void updateDisplay(int current, int max) {
        // 1. Update Teks
        String prefix = isMp ? "MP: " : "HP: ";
        valueLabel.setText(prefix + current + " / " + max);

        // 2. Hitung Persentase Lebar
        float percentage = (float) current / (float) max;
        if (percentage < 0) percentage = 0;
        if (percentage > 1) percentage = 1;

        // 3. Animasi lebar bar (langsung setWidth)
        // Kita harus mengambil Cell pembungkus image untuk mengubah lebarnya di dalam Table
        // Tapi cara paling aman di Scene2D adalah resize image-nya langsung jika fill settings benar,
        // atau update width cell-nya.

        // Update width image:
        barImage.setVisible(percentage > 0); // Sembunyikan jika 0 biar bersih

        // Trik resize di dalam Table:
        // Kita ambil Cell yang menyimpan barImage, lalu set width-nya
        // Cell cell = ((Table)barImage.getParent()).getCell(barImage);
        // cell.width(maxBarWidth * percentage);
        // barImage.getParent().invalidate(); // Request layout ulang

        // ATAU cara simpel LibGDX Image scaling:
        barImage.setWidth(maxBarWidth * percentage);
        barImage.invalidateHierarchy();

        // Cara paling reliable untuk bar: Pakai 'ProgressBar' bawaan LibGDX sebenernya,
        // tapi karena kita custom manual, kita ubah width cell container-nya saja di Update berikutnya
        // Untuk sekarang, pastikan barImage di-set Scaling-nya:
        barImage.setDrawable(new TextureRegionDrawable(createSolidTexture(barImage.getColor())));
        // Note: Kode di atas hanya re-create texture, visual update mungkin butuh Scaling.stretch

        // REVISI LOGIC RESIZE BAR YANG LEBIH STABIL:
        // Kita ubah scale X-nya saja
        barImage.setScaleX(percentage);
    }
}
