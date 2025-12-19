package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Factory.UIFactory;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class NarrativeScreen implements Screen {
    private Main game;
    private Stage stage;
    private Label nameLabel;
    private Label dialogLabel;

    // Membuat class baru agar tidak hanya menggunakan satu tipe string saja
    private static class StoryStep {
        String text;
        String backgroundPath = null;
        String soundtrackPath = null;
        String sfxPath = null;
        boolean triggerBattle = false; // Digunakan untuk menentukan kapan memulai battle

        // FIELD BARU UNTUK BATTLE
        EnemyType battleEnemy = null;
        String battleBackground = null;

        public StoryStep(String text) {
            this.text = text;
        }

        // Chaining
        public StoryStep setBackground(String path) {
            this.backgroundPath = path;
            return this;
        }
        public StoryStep setSoundtrack(String path) {
            this.soundtrackPath = path;
            return this;
        }
        public StoryStep setSoundEffect(String path) {
            this.sfxPath = path;
            return this;
        }
        public StoryStep setBattle(EnemyType enemy, String bgPath) {
            this.battleEnemy = enemy;
            this.battleBackground = bgPath;
            return this;
        }
    }

    // Helper 'line' agar memudahkan dalam memasukkan teks
    private static StoryStep line(String text) {
        return new StoryStep(text);
    }

    // Data Cerita Sederhana (Array of Steps)
    private StoryStep[] script = {
        line("Dear all, do you think there is even any meaning to this life?")
            .setBackground("Background/Dream.png")
            .setSoundtrack("Soundtrack/Dream.mp3"),
        line("Why should I live and continue this life? What's make life worth living?"),
        line("Someone said that you have to live the best of your life, but what does it mean by best?"),
        line("Is achieving something great really matters? If we already achieved it, then what's next?"),
        line("Is pleasure and happiness from achieving it actually the meaning itself?"),
        line("But, is happiness really ultimate meaning to our life?"),
        line("What is even a meaning? How do we consider something as a true meaning?"),
        line("I don't know..."),
        line("Can I even get out from this abyss of meaning?"),
        line("Well..."),
        line("Even to this day, when I always ponder on these questions, I still don't know what's the answer is..."),
        line("Maybe.."),
        line("Someday.."),
        line("I will find the answer"),
        line("...")
            .setSoundtrack("STOP"),
        line("......"),
        line("Librarian: 'Hey...'"),
        line("..."),
        line("Librarian: 'Hey, WAKE UP, do you know it is prohibited to sleep in library?'")
            .setBackground("Background/Library.jpg")
            .setSoundEffect("SFX/TableSlam.mp3"),
        line("MC: 'Alright-alright, I will wake up, jeez, do you know that there is a rule to not shouting in library?'")
            .setSoundtrack("Soundtrack/Library.mp3"),
        line("Librarian: 'It is an exception, sacrificing a little stability to prevent greater chaos is acceptable'"),
        line("MC: 'What the hell? If you said so, I guess I can't argue with that'"),
        line("This is one library in Faculty of Magecraft in University of Indonesia."),
        line("The existence of faculty itself is concealed from the public, only special members such as higher board of rectorate itself have privilege to know such faculty exist"),
        line("And if you think ordinary people can accidentally find it, then the chance is infinitesimal."),
        line("This faculty exist in different space-time spectrum."),
        line("Only those who have handful knowledge of magecraft can access it"),
        line("By the way, In this library, I just happen to like reading any books of my interests."),
        line("philosophy, math, computing, science, engineering, anatomy, psychology, magecraft theory, and eventually history itself."),
        line("Sometimes, it is just becoming of my habit to sleep in library while reading a book."),
        line("..."),
        line("There is sentence that's just lingering in my mind recently"),
        line("'Leaver dea as slaef.'")
            .setBackground("Background/BookPage.jpeg")
            .setSoundEffect("SFX/BookOpening.mp3"),
        line("It is a quote from a history book I read, English-wise it means 'it is better to die instead of being a slave'"),
        line("What does it mean by slave?"),
        line("MC: 'Hey, do you know the meaning of this part of sentence?'")
            .setBackground("Background/Library.jpg"),
        line("Librarian: 'I think this quote is saying about free will. Let me ask you a question, if your life is tied by someone will, would you accept it or rebel against it?'"),
        line("MC: 'I don't know, but here is my unpopular opinion, if someone who's tying their will is keeping them safe, well, and healthy, Is it bad to let them free since they could choose the wrong path that can destroy themself?'"),
        line("Librarian: 'Well, I guess that's a one way to interpret it, I think it is up to them what are they valuing more, free will of a sufferer or safety of a slave'"),
        line("MC: 'That makes sense. In modern society, there are those worker who forced to do what their supervisor said without even noticing or questioning if that's not part of their job description. What a straight slavery'"),
        line("Well, pondering about it desperately would never make me understand. I guess let's settle it for now."),
        line("And then there is this strange sign in my hand, it just abruptly appear out of nowhere some days ago, no pain or any anomaly resulting from this sign, only a sign that makes my appearance of hand peculiar, so i just hide it using gloves."),
        line("Realizing it has been already this late, i guess i need to go home."),
        line("I usually return to my home using public transport such as train and bus.")
            .setBackground("Background/TightAlley.jpeg")
            .setSoundtrack("Soundtrack/Decisions.mp3"),
        line("And i think the process of returning itself is satisfying."),
        line("I can contemplate anything i like. Thinking about it, i still don't know what is this sign in my hand..."),
        line("Just sometimes after contemplating, i just hearing something at this quiet alley..."),
        line("Suspicious Guy: ..."),
        line("It appears to be a suspicious guy using a strange suit..."),
        line("This guy suddenly just throwing a knife at me."),
        line("MC : 'Wha??'")
            .setSoundEffect("SFX/KnifeThrowing.mp3"),
        line("I just happen evading the attack using my body strengthening, i need to escape since this guy is trying to kill me."),
        line("I run..."),
        line("Keep running..."),
        line("But this thing is still chasing me until i have no energy left."),
        line("And suddenly at this moment, my time and reality just run very slowly and practically stopping..."),
        line("Unknown Voice: 'You are really on such predicament'"),
        line("MC: 'Who are you? And why the reality is just stopping?'"),
        line("Unknown Voice: 'Look at your hand, do you really not know what's the meaning of the sign?'"),
        line("MC: 'I really don't know, i am trying to search what is this'"),
        line("Unknown Voice: 'It is command spells, you are qualified as a master at incoming Holy Grail War'"),
        line("MC: 'Me, a master? I see'"),
        line("I just remember there is servant contract system in such a war where command spells is given to every master to command their servant, I think this is one of it huh, i don't know which war i was involved in and why i am being involved, but i guess lets finish this predicament first"),
        line("Unknown Voice: 'Hey, don't just stop like that, you have to summon me immediately, i could not accelerate our perception any longer, you don't wanna get killed don't you?'"),
        line("Well, at this point I don't have a choice to reject the involvement since I can die if rejecting it."),
        line("Not long after that, the reality and time itself is coming back to normal."),
        line("MC: 'Alright. Then I shalt summon thee... COME!!!'"),
        line("After spelling such command, appear a tall girl with a big two-handed sword ready at her hand ready to fight back a suspicious guy, from the image I get, i think my servant is Saber-class servant."),
        line("Saber: 'Hello there, let me handle this guy first'")
            .setBattle(EnemyType.SKELETON, "Background/TightAlley.jpeg"),
        line("Saber: 'Hey, you are Assassin right?"),
        line("Suspicious Guy: '...'"),
        line("Saber: 'Not answering anything... what a typical assassin'"),
        line("Suspicious Guy: [Escape and disappear]"),
        line("Saber: 'Tch, I guess we don't know anything huh?'"),
        line("Saber: 'Alright Master, let's get into safe place'"),
        line("Saber: Well Master, let me introduce myself. I am Pier Gerlofs Donia..."),
        line("Saber: 'Nice to meet you. Let's work this out together shall we? :D'")
    };
    private int scriptIndex = 0;

    // Asset Gambar, SFX, Karakter, dan Soundtrack untuk VN
    private Texture characterImg, suspiciousGuy;
    private Texture backgroundTexture;
    private Music currentSoundtrack;
    private String currentSoundtrackPath = "";

    public NarrativeScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        characterImg = ResourceManager.getInstance().getTexture("Entity/Player/MC.png");
        suspiciousGuy = ResourceManager.getInstance().getTexture("Entity/Enemy/suspiciousGuy.png");

        // PERUBAHAN DISINI: Ambil texture background dari Resource Manager
        backgroundTexture = ResourceManager.getInstance().getTexture("Background/Temps.png");

        setupUI();
        updateSceneData();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // 1. Buat Container Tabel Utama
        Table dialogBox = UIFactory.createDialogBox(skin);
        dialogBox.setSize(w - 40, h * 0.3f);
        dialogBox.setPosition(20, 20);

        // PENTING: Setting alignment tabel agar isi mulai dari kiri atas
        dialogBox.top().left().pad(20);

        // 2. Setup Label Nama
        nameLabel = new Label("Name", skin); // Jangan pakai UIFactory dulu biar wrap-nya mati
        nameLabel.setColor(Color.GOLD);
        nameLabel.setWrap(false); // PENTING: Matikan wrap agar nama tidak vertikal!

        // 3. Setup Label Dialog
        dialogLabel = new Label("...", skin);
        dialogLabel.setColor(Color.WHITE);
        dialogLabel.setWrap(true); // Dialog boleh wrap (turun baris kalau panjang)

        // 4. Masukkan ke Tabel dengan layout yang benar
        // Baris 1: Nama
        dialogBox.add(nameLabel).left().padBottom(10).row();

        // Baris 2: Dialog (Expand X agar memenuhi lebar)
        dialogBox.add(dialogLabel).left().width(w - 80).expandX();

        // Listener klik layar
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                advanceStory();
            }
        });

        stage.addActor(dialogBox);
    }

    private void advanceStory() {
        // Cek dulu, apakah step SEBELUMNYA (yang baru saja diklik) punya trigger battle?
        // Kita cek index yang sekarang sebelum ditambah
        StoryStep currentStep = script[scriptIndex];

        if (currentStep.battleEnemy != null) {
            // ADA BATTLE!
            triggerBattle(currentStep);
            // Jangan increment scriptIndex dulu, atau increment disini tergantung logika.
            // Strategi terbaik: Masuk battle -> Menang -> Baru increment index.
            return;
        }

        // Kalau tidak ada battle, lanjut text biasa
        scriptIndex++;

        if (scriptIndex >= script.length) {
            // CERITA HABIS -> PINDAH KE WORLD MAP
            game.setScreen(new WorldMapScreen(game));

            // Opsional: Dispose narrative screen agar hemat memori karena sudah tidak dipakai
            // dispose();
        } else {
            updateSceneData();
        }
    }

    private void triggerBattle(StoryStep step) {
        // Kita simpan instance NarrativeScreen ini ('this')
        final NarrativeScreen narrativeInstance = this;

        // Buat Callback: Apa yang terjadi setelah menang?
        Runnable onVictory = new Runnable() {
            @Override
            public void run() {
                // 1. Balikkan Layar ke Narrative Screen ini
                game.setScreen(narrativeInstance);

                // 2. Maju ke baris dialog berikutnya (biar gak ngulang battle)
                scriptIndex++;
                if (scriptIndex < script.length) {
                    updateSceneData();
                } else {
                    game.setScreen(new WorldMapScreen(game));
                }
            }
        };

        // Pindah ke BattleScreen dengan membawa Callback tadi
        // Pastikan BattleScreen Anda sudah punya constructor (String bg, EnemyType type, Runnable onVictory)
        game.setScreen(new BattleScreen(game, step.battleBackground, step.battleEnemy, onVictory));
    }

    private void updateSceneData() {
        StoryStep currentStep = script[scriptIndex];
        String newPath = currentStep.soundtrackPath;

        if (currentStep.backgroundPath != null) {
            backgroundTexture = ResourceManager.getInstance().getTexture(currentStep.backgroundPath);
        }
        if ("STOP".equals(newPath)) {
            if (currentSoundtrack != null) {
                currentSoundtrack.stop(); // Mematikan musik
            }
            currentSoundtrack = null;     // Mengosongkan variabel object musik
            currentSoundtrackPath = "STOP"; // Menandai bahwa status sekarang adalah hening
        }
        else if (currentStep.soundtrackPath != null && !currentStep.soundtrackPath.equals(currentSoundtrackPath)) {
            if (currentSoundtrack != null) currentSoundtrack.stop();

            currentSoundtrack = ResourceManager.getInstance().getMusic(currentStep.soundtrackPath);
            currentSoundtrackPath = currentStep.soundtrackPath;

            if (currentSoundtrack != null) {
                currentSoundtrack.setLooping(true);
                currentSoundtrack.setVolume(0.5f);
                currentSoundtrack.play();
            }
        }

        if (currentStep.sfxPath != null) {
            Sound sfx = ResourceManager.getInstance().getSound(currentStep.sfxPath);
            sfx.play(1.0f); // Volume penuh
        }

        String line = currentStep.text;
        String[] parts = line.split(": ", 2);

        if (parts.length == 2) {
            nameLabel.setText(parts[0]);
            dialogLabel.setText(parts[1]);
        } else {
            nameLabel.setText("");
            dialogLabel.setText(line);
        }
    }

    // drawCharacter() digunakan untuk memudahkan render karakter (helper)
    private void drawCharacter(Texture texture, boolean isRightSide) {
        float width = 300;
        float height = 450;
        float padding = 50;
        float x;

        if (isRightSide) {
            x = Gdx.graphics.getWidth() - width - padding;
        } else {
            x = padding;
        }

        stage.getBatch().draw(texture,
            x, 0, width, height,
            0, 0, texture.getWidth(), texture.getHeight(),
            isRightSide, false
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Background Hitam (atau kasih gambar hutan nanti)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        String currentText = script[scriptIndex].text; // Menggunakan currentTeks agar mudah dibaca

        if (currentText.startsWith("MC")) {
            drawCharacter(characterImg, false);
        }
        else if (currentText.startsWith("Suspicious Guy")) {
            drawCharacter(suspiciousGuy, true);
        }
        stage.getBatch().end();

        // Update dan Gambar UI (Kotak Dialog) PALING TERAKHIR (Layer teratas)
        stage.act(delta);
        stage.draw();
    }

    // Boilerplate standard
    @Override
    public void show() {
        // PENTING: Kembalikan input processor ke stage ini
        // Karena saat battle, input diambil oleh BattleScreen
        Gdx.input.setInputProcessor(stage);

        // Jika musik mati saat hide, nyalakan lagi jika perlu
        if (currentSoundtrack != null && !currentSoundtrack.isPlaying()) {
            currentSoundtrack.play();
        }
    }

    @Override
    public void hide() {
        // Matikan musik saat pindah ke Battle agar tidak tabrakan dengan BGM Battle
        if (currentSoundtrack != null) {
            currentSoundtrack.pause(); // Gunakan pause, bukan stop, agar lanjut lagunya
        }
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); }
}
