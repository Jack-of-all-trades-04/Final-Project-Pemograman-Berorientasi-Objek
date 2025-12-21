package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Factory.UIFactory;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
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
    private int currentChapterId;

    // Membuat class baru agar tidak hanya menggunakan satu tipe string saja
    private static class StoryStep {
        String text;
        String backgroundPath = null;
        String soundtrackPath = null;
        String sfxPath = null;

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
    private StoryStep[] script;
    private int scriptIndex = 0;

    // Asset Gambar, SFX, Karakter, dan Soundtrack untuk VN
    private Texture characterImg, suspiciousGuy, saberImg, pretenderImg, lancerImg, lancerBattleImg, ameliaImg, riderImg, charlotteImg, casterImg, fionaImg;
    private Texture backgroundTexture;
    private Music currentSoundtrack;
    private String currentSoundtrackPath = "";

    // Helper untuk mengambil texture
    private Texture getTex(String path) {
        return ResourceManager.getInstance().getTexture(path);
    }

    public NarrativeScreen(Main game, int chapterID) {
        this.game = game;
        this.currentChapterId = chapterID;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        characterImg = getTex("Entity/Player/MC.png");
        suspiciousGuy = getTex("Entity/Enemy/suspiciousGuy.png");
        saberImg = getTex("Entity/Player/Saber.png");
        pretenderImg = getTex("Entity/Character/Tlaloc.png");
        ameliaImg = getTex("Entity/Character/Amelia.png");
        lancerImg = getTex("Entity/Character/Noel.png");
        lancerBattleImg = getTex("Entity/Character/NoelBattle.png");
        riderImg = getTex("Entity/Character/Rider.png");
        charlotteImg = getTex("Entity/Character/Charlotte.png");
        casterImg = getTex("Entity/Character/Caster.png");
        fionaImg = getTex("Entity/Character/Fiona.png");

        // PERUBAHAN DISINI: Ambil texture background dari Resource Manager
        backgroundTexture = getTex("Background/Temps.png");

        this.script = loadChapterData(chapterID);

        setupUI();
        updateSceneData();
    }

    private StoryStep[] loadChapterData(int chapterId) {
        switch (chapterId) {
            case 1:
                return getPrologueScript(); // Prolog atau Chapter 1
            case 2:
                return getChapterTwoScript(); // Chapter 2
            case 3:
                return getChapterThreeScript(); // Chapter 3
            case 4:
                return getChapterFourScript(); // Chapter 4
            case 5:
                return getChapterFinalScript(); // Final battle atau Chapter 5
            default:
                // Fallback jika ID salah
                return new StoryStep[] {
                    line("Error: Chapter data not found.").setBackground("Background/Blackscreen.jpg")
                };
        }
    }
    // Template
    /*private StoryStep[] getChapterOneScript() {
        return new StoryStep[]{

        };
    }*/

    private StoryStep[] getPrologueScript() {
        return new StoryStep[]{
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
            line("I hope I will find the answer…"),
            line("...")
                .setSoundtrack("STOP"),
            line("......"),
            line("Librarian: 'Hey...'"),
            line("..."),
            line("Librarian: 'Hey, WAKE UP, do you know it is prohibited to sleep in library?'")
                .setBackground("Background/Library.jpg")
                .setSoundEffect("Audio/Sound_Effect/TableSlam.mp3"),
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
            line("Philosophy, math, computing, science, engineering, anatomy, psychology, magecraft theory, and eventually history itself."),
            line("Sometimes, it is just becoming of my habit to sleep in library while reading a book."),
            line("..."),
            line("There is sentence that's just lingering in my mind recently"),
            line("'Leaver dea as slaef.'")
                .setBackground("Background/BookPage.jpeg")
                .setSoundEffect("Audio/Sound_Effect/BookOpening.mp3"),
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
            line("Just sometimes after contemplating, i just hear something in this quiet alley..."),
            line("Suspicious Guy: ..."),
            line("It appears to be a suspicious guy using a strange suit..."),
            line("This guy suddenly just threw a knife at me."),
            line("MC : 'Wha??'")
                .setSoundEffect("Audio/Sound_Effect/KnifeThrowing.mp3"),
            line("I just happened to evade the attack by using my body strengthening,"),
            line("Let's evaluate this situation, their speed is terrifying, I hardly see the knife. My last evasion is also really lucky."),
            line("I really need to get out of here quickly, it is an opponent that i can't face directly."),
            line("I run..."),
            line("Keep running..."),
            line("But this thing is still chasing me until i have no energy left."),
            line("I run anywhere safe until I got into this open space.")
                .setBackground("Background/DarkPark.jpeg"),
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
                .setBattle(EnemyType.ASSASSIN, "Background/DarkPark.jpeg"),
            line("Saber: 'Hey, you are Assassin right?"),
            line("Suspicious Guy: '...'"),
            line("Saber: 'Not answering anything... what a typical assassin'"),
            line("Suspicious Guy: [Escape and disappear]"),
            line("Saber: 'Tch, I guess we don't know anything huh?'"),
            line("Saber: 'Alright Master, let's get into safe place'"),
            line("Saber: 'Hmm hmm, what a nice room you have'")
                .setBackground("Background/Room.jpeg"),
            line("Saber: 'Well Master, let me introduce myself. I am Pier Gerlofs Donia, a saber-class servant summoned to the current Holy Grail War.'"),
            line("Saber: 'Nice to meet you. Let's work this out together shall we? :D'"),
            line("And that’s how I met my servant for the first time some days ago.")
                .setBackground("Background/Blackscreen.jpg")
                .setSoundtrack("STOP"),
            line("Now, I am here in Yogyakarta where the Holy Grail War happens. It just feels surreal that my life turns dramatically like this.")
                .setBackground("Background/RoyalCapital.jpeg")
        };
    }

    private StoryStep[] getChapterTwoScript() {
        return new StoryStep[]{
            line("[Somewhere in the shrine]")
                .setBackground("Background/Shrine.jpeg")
                .setSoundtrack("Soundtrack/Mystical.mp3"),
            line("Sparking...")
                .setSoundEffect("Audio/Sound_Effect/Spark.mp3"),
            line("???: 'So, you have summoned me huh? I guess you are my master'"),
            line("Charlotte: 'I see, analyzing into your pattern of magical energy and origin, can I conclude that you are Rider?'"),
            line("Charlotte: 'No, your origin name is Prabu Siliwangi right?'"),
            line("Siliwangi: 'I see, so this is my master huh, how magnificent for recognizing my name with just magical analysis"),
            line("Siliwangi: 'Alright, for this war then i will help you achieve victory'"),
            line("[Somewhere in the castle]")
                .setBackground("Background/Castle.jpeg")
                .setSoundtrack("Soundtrack/Ominous.mp3"),
            line("Berserker: 'So, what do you actually wish from this war, master?'"),
            line("???: 'Unification of the world in one true power, only by achieving the omnipotent grail I can do that'"),
            line("Berserker: 'Why do you want to unify this world?'"),
            line("???: 'In my mind, this world is just too bleak, there is so much suffering, war, and injustice that always happens.'"),
            line("???: 'By gaining the grail, I can chain all humanity in one state of rule so nobody can cause any mistreatment to each other. Safety through absolute control.'"),
            line("Berserker: 'I see, then I shall abide to your will and work through that'"),
            line("[Somewhere in the capital]")
                .setBackground("Background/RoyalCapital.jpeg")
                .setSoundtrack("Soundtrack/WorldMap.mp3"),
            line("MC: 'Alright, let’s do our intel gathering, we can go into the hidden library that only mages can access'"),
            line("Saber: 'What do we search there?'"),
            line("MC: 'Some info about the current location of mysteries, map, or anything related to this war.'"),
            line("Saber: 'Hmm hmm, why do we just declare ourself and wait for the enemy?'"),
            line("MC: 'Aw hell nah.'"),
            line("Saber: 'Of course, I am joking, please don’t take it seriously.'"),
            line("After that, we just travel to the library and at the same time someone just call our name"),
            line("???: Hey, 'you are master and servant right?'"),
            line("!!!"),
            line("Someone just recognizes our status."),
            line("Little Girl: 'Lancer, attack that servant.'"),
            line("Lancer: 'Alright.'"),
            line("Lancer (Battle): 'Preparing combat mode..'")
                .setBattle(EnemyType.LANCER, "Background/RoyalCapital.jpeg"),
            line("Lancer: 'Ugh... damn, this Saber is just too strong!'"),
            line("Amelia: 'You are right, Lancer.'"),
            line("MC: Do you guys want to continue this fight?'"),
            line("Amelia: 'Well no. At this point, let’s work this out together.'"),
            line("We decided to talk"),
            line("Amelia: 'Firstly, my name is Amelia, master of Lancer. I am joining this war to get medicine and a relic that can help my magical research. What about you?'"),
            line("MC: 'Well, I don't know. I guess I just do it to fulfill my duty as a Master.'"),
            line("Amelia: 'What? Don’t bullshit me. Are you really risking your life for something like that? I couldn't accept that. What is your true wish?'"),
            line("MC: 'It is true though. Well, if I can word it... then maybe my wish is to find my true wish?'"),
            line("Amelia: 'You are conflicting yourself. But at least you have a goal, though.'")
        };
    }

    private StoryStep[] getChapterThreeScript() {
        return new StoryStep[]{
            line("Chapter 3: The Calculated Alliance")
                .setBackground("Background/Kalimba Forest.png")
                .setSoundtrack("Soundtrack/Mystical.mp3"),
            line("We formed a temporary party with Amelia and Lancer to secure the coastline. However, our path was blocked not by Caster, but by a dignified presence"),
            line("A woman in a formal suit stands calmly in the clearing. Beside her stands a man radiating the aura of a King."),
            line("Charlotte: 'Halt. This area is under El-Melloi observation.'"),
            line("Rider (Prabu Siliwangi): 'Young warriors. You carry a heavy scent of blood.'"),
            line("MC: 'We are looking for Caster. We don't want trouble.'"),
            line("Charlotte: 'Naive. In this war, weakness is a sin. If you cannot pass us, you will only be fodder for Caster.'"),
            line("Charlotte: 'Rider. Test them.'"),
            line("Rider: 'Hahaha! Very well! Show me your resolve, young Master! If your will is weak, my blade shall shatter it!'"),
            line("Saber: 'A King, huh? Pier Gerlofs Donia accepts your challenge!'")
                .setBattle(EnemyType.RIDER,"Background/Kalimba Forest.png"),
            line("Battle between servants, Saber and Rider clash blades one last time, creating a shockwave that clears the trees around us. Neither backs down."),
            line("Rider: 'Hahaha! Excellent! Your sword is honest, Saber! And you, young Master, your orders were decisive.'"),
            line("Charlotte: 'That's enough, Rider. They passed.'"),
            line("MC: 'You... stopped?'"),
            line("Charlotte: 'My goal is to stop the irregularity of this war caused by the 'Administrator'. I need capable allies, not dead weight. You have proven your worth.'"),
            line("MC: 'So, it was a test.'"),
            line("Rider: 'Indeed. Let us fight side by side.'"),
            line("Before we could celebrate our new alliance, the sea began to boil"),
            line("Caster (Kadita): 'How noisy. Can't a Queen sleep?'"),
            line("Sultan: 'Intruders... drown them all.'"),
            line("Rider: 'The Queen of the South. I shall handle thi-"),
            line("BOOOM!! Before Rider could move, a blue flash of lightning struck Caster from the sky. The Queen was vaporized instantly.")
                .setSoundEffect("SFX/ThunderExplosion.mp3"),
            line("We look up to the cliff. Fiona and Archer are standing there"),
            line("Fiona: 'Target neutralized. Efficient, isn't it?'"),
            line("MC: 'You..'"),
            line(" Fiona: 'I am Fiona. Since you survived Rider's test, I calculate a 48% success rate if we join forces.'"),
            line("And thus, the Great Alliance was formed.")
        };
    }

    private StoryStep[] getChapterFourScript() {
        return new StoryStep[]{
            line("We gathered at Aliz Ruins. It was a sight to behold - four Masters and four Servants united."),
            line("Fiona: 'Raja Jawa is in the Floating Fortress. Here is the plan.'"),
            line("Fiona: 'Rider will use his Noble Phantasm to breach the main gate. Saber and Lancer will handle the guards. Archer will provide long-range support.'"),
            line("Rider: 'A sound plan. My chariot shall crush their defenses.'"),
            line("Amelia: 'Finally... we can end this.'"),
            line("Suddenly, the air turns cold.")
                .setSoundtrack("STOP"),
            line("Assassin: 'My, my~ A full course meal served on a silver platter.'")
                .setBattle(EnemyType.ASSASSIN,"Background/Aliz Ruins.png"),
            line("Raja Jawa (Voice): 'Disappointing. Burn.'"),
            line("The sky turns red. The orbital bombardment begins."),
            line("The beam falls. We have no time to run."),
            line("Archer: 'Pashupata!!'"),
            line("Archer sacrifices himself to block the beam. He fades away with a smile."),
            line("Assassin lunges at the shocked Fiona."),
            line("Rider: 'Not on my watch!'"),
            line("Rider throws his body in front of Fiona, taking the cursed blade straight to his heart."),
            line("Charlotte: 'Rider!!'"),
            line("Rider: 'Live... on...'"),
            line("Lancer grabs the laughing Assassin."),
            line("Lancer: 'Die with me!'"),
            line("In an instant, our alliance was shattered. Only Saber remained."),
            line("Fiona: 'My calculations... everyone...'"),
            line("MC: 'I will kill him. I swear... I will kill Raja Jawa!'")
        };
    }

    private StoryStep[] getChapterFinalScript() {
        return new StoryStep[]{
            line("Ini chapter final atau 5"),
            line("Only two servants remain. My Saber... and the mysterious Pretender. We tracked the source of the anomaly to a floating fortress above the capital. The Master of Pretender, the one calling himself Raja Jawa, awaits."),
            line("Raja Jawa: 'Welcome. You are the last obstacle.'"),
            line("MC: 'You are the one behind this... the one who wants to unify the world?'"),
            line("Raja Jawa: 'Correct. Look at the tragedy below. Suffering caused by 'Choice'. By 'Ambition'.'"),
            line("Raja Jawa: 'I will use the Grail to strip humanity of that burden. No more choice. No more pain. Everyone will be safe under my rule.'"),
            line("Beside him stands Pretender (Tlaloc), radiating an ominous aura."),
            line("MC: 'That's not safety. That's just a farm! And we are the cattle!'"),
            line("Raja Jawa: 'It is peace.'"),
            line("MC: 'No. It's death of the soul!'"),
            line("I step forward, no longer afraid."),
            line("I spent my life wondering why I exist. Searching for a 'best' life. I realized... there is no 'best'. There is only 'my' life."),
            line("MC: 'If you take away my choice to suffer, to fail, to cry... you take away my life!'"),
            line("MC: 'I would rather die as a free man than live as your safe slave! Leaver dea as slaef!'"),
            line("Raja Jawa: 'Foolish. Tlaloc... Destroy them.'"),
            line("Tlaloc: 'Alright'")
                .setBattle(EnemyType.PRETENDER, "Background/Library.jpg"),
            line("The castle is crumbling. Saber is kneeling, his great sword shattered. Pretender stands tall, barely scratched. The difference in power was absolute."),
            line("Saber: 'Hah... Hah... I can't... cut through... that armor...'"),
            line("Raja Jawa: 'See? Willpower means nothing before absolute power.'"),
            line("MC: 'Saber...'"),
            line(" Saber: 'Master... I have one strike left. It will cost my spirit origin. I will vanish.'"),
            line("MC: 'Saber... you...'"),
            line("Saber: 'But it will open a path for you. To strike him.'"),
            line("MC: 'Do it. Let's show them.'"),
            line("Saber: 'GRUTTE PIER!!!'"),
            line("Saber burns his very soul into a massive swing. It doesn't kill Pretender. But it smashes Pretender aside, pinning the servant to the wall."),
            line("Saber: 'NOW, MASTER!'"),
            line("I run past the servant, straight towards Raja Jawa."),
            line("Raja Jawa: 'You dare approach me? A mere human?'"),
            line("MC: 'I am a human! That's why I struggle!'"),
            line("I punch Raja Jawa with everything I have."),
            line("The punch I pull up using strengthening is powerful enough to kill him"),
            line("At last we survived the fall. The Grail War is over. The true nature of the grail itself is very destructive, I can't rely on it."),
            line("I look around. Saber is still here, his form transparent, fading. Pretender pulls herself from the rubble. She is still alive. Raja Jawa is gone."),
            line("Saber: 'Heh... looks like... I survived... but barely.'"),
            line("MC: 'Saber...'"),
            line("Saber: 'And that one... keeps living too.'"),
            line("Pretender: ..."),
            line("Pretender turns around and walks away into the mist. She has no Master now, but she survives."),
            line("So, in the end... Caster is dead. Rider is dead. Archer is dead. Lancer is dead. Assassin is dead. Only Saber and Pretender remain in this scarred world."),
            line("MC: 'Saber.'"),
            line("Saber: 'Yes, Master?'"),
            line("MC: 'I still don't know the ultimate meaning of life.'"),
            line("MC: 'But I know that I want to live to find it. I want to choose tomorrow.'"),
            line("She smiles. A genuine, big smile."),
            line("Saber: 'That is enough, Mike. That is enough.'"),
            line("'Leaver dea as slaef'. I chose to live. And I am free.")
        };
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

            // Inkremen unlockedChapter
            finishAndExit();
            // Opsional: Dispose narrative screen agar hemat memori karena sudah tidak dipakai
            // dispose();
        } else {
            updateSceneData();
        }
    }

    private void finishAndExit() {
        int currentProgress = game.playerStats.getUnlockedChapter();

        // Cek: Apakah kita perlu update?
        // Gunakan logika '<' agar lebih aman (misal current=1, finish=1 -> set jadi 2)
        if (currentProgress < this.currentChapterId + 1) {

            int nextChapter = this.currentChapterId + 1;
            game.playerStats.setUnlockedChapter(nextChapter);
            System.out.println("STORY FINISHED! Saving & Unlocking Chapter " + nextChapter + "...");

            // PANGGIL SAVE DENGAN CALLBACK
            // Layar TIDAK AKAN PINDAH sampai Save berhasil.
            com.FEA_3.frontend.Utils.NetworkManager.getInstance().savePlayer(
                "User1",
                game.playerStats,
                new com.FEA_3.frontend.Utils.NetworkManager.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Save Confirmed. Returning to Map.");
                        game.setScreen(new WorldMapScreen(game));
                    }

                    @Override
                    public void onFail(String msg) {
                        System.err.println("Save Failed! " + msg);
                        // Tetap pindah layar meski gagal save (biar gak nyangkut),
                        // tapi user mungkin kehilangan progress.
                        game.setScreen(new WorldMapScreen(game));
                    }
                }
            );

        } else {
            // Jika ini Replay (sudah pernah tamat), langsung keluar tanpa save
            System.out.println("Replay finished. Returning to Map.");
            game.setScreen(new WorldMapScreen(game));
        }

        // 2. Transisi dengan Jeda (PENTING UNTUK MENCEGAH RACE CONDITION)
        // Kita beri waktu 0.5 detik agar 'savePlayer' selesai dikirim sebelum WorldMap me-load data lagi.
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new WorldMapScreen(game));
            }
        }, 0.5f);
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
                    finishAndExit();
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
        float width = 420;
        float height = 570;
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

        // --- PERBAIKAN DISINI ---
        // Cek apakah scriptIndex masih dalam batas aman?
        if (scriptIndex < script.length) {

            // Ambil text hanya jika index valid
            String currentText = script[scriptIndex].text;

            if (currentText.startsWith("MC")) {
                drawCharacter(characterImg, false);
            }
            else if (currentText.startsWith("Suspicious Guy")) {
                drawCharacter(suspiciousGuy, true);
            }
            else if (currentText.startsWith("Saber")) {
                drawCharacter(saberImg, false);
            }
            else if (currentText.startsWith("Berserker") || currentText.startsWith("Pretender")) {
                drawCharacter(pretenderImg, true);
            }
            else if (currentText.startsWith("Little Girl") || currentText.startsWith("Amelia")) {
                drawCharacter(ameliaImg, true);
            }
            else if (currentText.startsWith("Lancer (Battle)")) {
                drawCharacter(lancerBattleImg, true);
            }
            else if (currentText.startsWith("Lancer")) {
                drawCharacter(lancerImg, true);
            }
        }
        else if (currentText.startsWith("Rider")) {
            drawCharacter(riderImg, true);
        }
        else if (currentText.startsWith("Charlotte")) {
            drawCharacter(charlotteImg, true);
        }
        else if (currentText.startsWith("Caster")) {
            drawCharacter(casterImg, true);
        }
        else if (currentText.startsWith("Fiona")) {
            drawCharacter(fionaImg, true);
        }
        // ------------------------
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
