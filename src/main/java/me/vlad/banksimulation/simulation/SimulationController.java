package me.vlad.banksimulation.simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import me.vlad.banksimulation.core.Bank;
import me.vlad.banksimulation.core.human.Clerk;
import me.vlad.banksimulation.core.human.Client;
import me.vlad.banksimulation.util.ValueRange;
import me.vlad.banksimulation.util.adapters.DurationTypeAdapter;
import me.vlad.banksimulation.util.adapters.LocalDateTimeTypeAdapter;
import me.vlad.banksimulation.util.adapters.LocalDateTypeAdapter;
import me.vlad.banksimulation.util.adapters.LocalTimeTypeAdapter;
import me.vlad.banksimulation.visual.BankDrawer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class SimulationController {
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    private static String[] surnames = new String[] { "Абрамов", "Авдеев", "Агеев", "Акимов", "Аксенов", "Александров", "Алексеев", "Алешин", "Андреев", "Андрианов", "Анисимов", "Антонов", "Артамонов", "Архипов", "Астафьев", "Афанасьев", "Балашов", "Баранов", "Белов", "Беляев", "Березин", "Беспалов", "Бирюков", "Блохин", "Богданов", "Борисов", "Бочаров", "Булгаков", "Буров", "Быков", "Васильев", "Виноградов", "Винокуров", "Власов", "Волков", "Воробьев", "Высоцкий", "Гаврилов", "Галкин", "Герасимов", "Голубев", "Горбачев", "Горбунов", "Горохов", "Горшков", "Грачев", "Григорьев", "Громов", "Губанов", "Гуляев", "Гусев", "Дементьев", "Демьянов", "Денисов", "Дмитриев", "Долгов", "Дроздов", "Дубровин", "Евдокимов", "Евсеев", "Егоров", "Еремин", "Ермаков", "Ермолов", "Ершов", "Ефимов", "Ефремов", "Жданов", "Журавлев", "Завьялов", "Зайцев", "Захаров", "Зеленин", "Зиновьев", "Золотарев", "Зубов", "Иванов", "Игнатов", "Игнатьев", "Ильин", "Ильинский", "Исаев", "Исаков", "Казаков", "Казанцев", "Калинин", "Карасев", "Карпов", "Карташов", "Киселев", "Клюев", "Ковалев", "Козлов", "Козырев", "Колесников", "Колосов", "Комаров", "Кондратьев", "Коновалов", "Константинов", "Корнев", "Корнеев", "Коровин", "Королев", "Костин", "Котов", "Кочетков", "Кошелев", "Круглов", "Крылов", "Кудрявцев", "Кузнецов", "Кузьмин", "Кулаков", "Кулешов", "Куликов", "Куприянов", "Лавров", "Лазарев", "Лапин", "Лебедев", "Левин", "Логинов", "Лопатин", "Лукьянов", "Лыков", "Львов", "Макаров", "Максимов", "Малышев", "Мальцев", "Маркелов", "Марков", "Мартынов", "Масленников", "Матвеев", "Медведев", "Мельников", "Мешков", "Мещеряков", "Миронов", "Митрофанов", "Михайлов", "Михеев", "Моргунов", "Морозов", "Муравьев", "Наумов", "Нестеров", "Никитин", "Никифоров", "Николаев", "Никонов", "Новиков", "Носков", "Носов", "Овсянников", "Овчинников", "Озеров", "Орехов", "Орлов", "Осипов", "Островский", "Павлов", "Панин", "Панков", "Панкратов", "Панов", "Пантелеев", "Панфилов", "Пахомов", "Петров", "Петровский", "Петухов", "Пименов", "Платонов", "Плотников", "Поздняков", "Покровский", "Поляков", "Пономарев", "Попов", "Потапов", "Прокофьев", "Пугачев", "Раков", "Рогов", "Родионов", "Рожков", "Романов", "Руднев", "Русаков", "Савин", "Сафонов", "Свиридов", "Седов", "Селиванов", "Семенов", "Сергеев", "Сидоров", "Симонов", "Синицын", "Скворцов", "Смирнов", "Соболев", "Соколов", "Соловьев", "Сомов", "Сорокин", "Сотников", "Софронов", "Стариков", "Старостин", "Степанов", "Субботин", "Суслов", "Сухарев", "Сычев", "Тарасов", "Терехов", "Тимофеев", "Титов", "Тихомиров", "Тихонов", "Трифонов", "Трошин", "Туманов", "Уткин", "Ушаков", "Фадеев", "Федоров", "Федотов", "Фетисов", "Филатов", "Филиппов", "Фомин", "Фролов", "Харитонов", "Царев", "Чернов", "Чистяков", "Шаповалов", "Шилов", "Широков", "Ширяев", "Шульгин", "Щербаков", "Щукин", "Юдин", "Яковлев" };
    private static String[] names = new String[] { "Адам", "Адриан", "Александр", "Алексей", "Али", "Альберт", "Анатолий", "Андрей", "Антон", "Аркадий", "Арсен", "Арсений", "Артемий", "Артур", "Артём", "Билал", "Богдан", "Борис", "Вадим", "Валерий", "Василий", "Виктор", "Виталий", "Владимир", "Владислав", "Всеволод", "Вячеслав", "Георгий", "Герман", "Глеб", "Гордей", "Григорий", "Давид", "Дамир", "Даниил", "Данил", "Данила", "Даниль", "Даниэль", "Демид", "Демьян", "Денис", "Дмитрий", "Евгений", "Егор", "Елисей", "Захар", "Ибрагим", "Иван", "Игнат", "Игорь", "Илья", "Камиль", "Карим", "Кирилл", "Клим", "Константин", "Лев", "Леон", "Леонид", "Лука", "Макар", "Максим", "Марат", "Марк", "Марсель", "Мартин", "Матвей", "Мирон", "Мирослав", "Михаил", "Назар", "Никита", "Николай", "Олег", "Павел", "Платон", "Пётр", "Рафаэль", "Роберт", "Родион", "Роман", "Ростислав", "Руслан", "Рустам", "Савва", "Савелий", "Святослав", "Семён", "Серафим", "Сергей", "Станислав", "Степан", "Стефан", "Тигран", "Тимофей", "Тимур", "Тихон", "Филипп", "Фёдор", "Эмиль", "Эмин", "Эмир", "Эрик", "Юрий", "Яков", "Ян", "Яромир", "Ярослав" };
    private static String[] patronymics = new String[] { "Адамович", "Александрович", "Алексеевич", "Алиевич", "Альбертович", "Андреевич", "Антонович", "Арсенович", "Арсентьевич", "Артемьевич", "Артурович", "Артёмович", "Билалович", "Богданович", "Вадимович", "Валерьевич", "Васильевич", "Викторович", "Витальевич", "Владимирович", "Владиславович", "Всеволодович", "Вячеславович", "Георгиевич", "Германович", "Глебович", "Гордеевич", "Григорьевич", "Давидович", "Дамирович", "Даниилович", "Данилович", "Даниэльевич", "Демидович", "Денисович", "Дмитриевич", "Евгеньевич", "Егорович", "Елисеевич", "Захарович", "Иванович", "Игоревич", "Ильич", "Кириллович", "Константинович", "Леонидович", "Леонович", "Лукич", "Львович", "Макарович", "Максимович", "Маратович", "Маркович", "Мартинович", "Матвеевич", "Миронович", "Мирославович", "Михайлович", "Никитич", "Николаевич", "Олегович", "Павлович", "Петрович", "Платонович", "Робертович", "Родионович", "Романович", "Русланович", "Савельевич", "Святославович", "Семёнович", "Сергеевич", "Станиславович", "Степанович", "Тимофеевич", "Тимурович", "Тихонович", "Филиппович", "Фёдорович", "Эмирович", "Эрикович", "Юрьевич", "Яковлевич", "Ярославович" };
    private transient Random random;
    private final int seed;
    private final Bank bank;
    private final Duration durationOfSimulation;
    private long secondsStep;
    private double dailyClerkSalary;
    private final ValueRange<Long> requestDistributionInterval;
    private final ValueRange<Long> durationOfRequestServicing;
    private final ValueRange<Double> profitOfRequestServicing;
    private LocalDateTime lastGeneratedRequest;
    private transient final Timeline simulationTimeline;
    private transient final BankDrawer bankDrawer;
    private transient final Canvas canvas;
    private boolean isPause;
    public SimulationController(int seed, Duration durationOfSimulation, long secondsStep, double dailyClerkSalary, ValueRange<Long> requestDistributionInterval, ValueRange<Long> durationOfRequestServicing, ValueRange<Double> profitOfRequestServicing, Canvas canvas) {
        this.seed = seed;
        this.durationOfSimulation = durationOfSimulation;
        this.secondsStep = secondsStep;
        this.dailyClerkSalary = dailyClerkSalary;
        this.requestDistributionInterval = requestDistributionInterval;
        this.durationOfRequestServicing = durationOfRequestServicing;
        this.profitOfRequestServicing = profitOfRequestServicing;
        random = new Random(seed);
        bank = new Bank(random.nextInt(10, 26), random.nextInt(2, 8));
        lastGeneratedRequest = null;
        generateClerks(bank, random, bank.getMaxClerksCount(), dailyClerkSalary);
        this.canvas = canvas;
        double tW = canvas.getWidth()*3/4;
        double tH = canvas.getHeight()*3/4;
        bankDrawer = new BankDrawer(bank, (canvas.getWidth() - tW)/2, (canvas.getHeight() - tH)/2,tW, tH);
        canvas.setOnMouseMoved(bankDrawer::mouseMovedEvent);
        simulationTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.ONE, actionEvent -> bankDrawer.draw(canvas.getGraphicsContext2D())),
                new KeyFrame(javafx.util.Duration.ONE, actionEvent -> bankDrawer.drawTooltip(canvas.getGraphicsContext2D())),
                new KeyFrame(javafx.util.Duration.millis(20)),
                new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> clearRect()),
                new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> temp())
        );
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    public void pause() {
        isPause = true;
    }
    public void resume() {
        isPause = false;
    }
    private void life() {
        bank.life(secondsStep);
    }
    private void tryGenerateRequest() {
        if(!bank.isWork())
            return;
        if(lastGeneratedRequest == null) {
            lastGeneratedRequest = bank.getCurrentDateTime();
            getRandomClient(random).comeToBank(bank, Duration.ofSeconds(durationOfRequestServicing.getRandomValueFromRange(random)), profitOfRequestServicing.getRandomValueFromRange(random));
            return;
        }
        long range = (long) (requestDistributionInterval.getRandomValueFromRange(random) * bank.getReputation());
        while(Duration.between(lastGeneratedRequest, bank.getCurrentDateTime()).toSeconds() >= range && !bank.isQueueFull()) {
            getRandomClient(random).comeToBank(bank, Duration.ofSeconds(durationOfRequestServicing.getRandomValueFromRange(random)), profitOfRequestServicing.getRandomValueFromRange(random));
            lastGeneratedRequest = lastGeneratedRequest.plusSeconds(range);
            range = (long) (requestDistributionInterval.getRandomValueFromRange(random) * bank.getReputation());
        }
    }
    public void setSecondsStep(long value) {
        secondsStep = value;
    }
    private void temp() {
        if(!isDone() && bankDrawer.isFixit() && !isPause) {
            tryGenerateRequest();
            life();
        }
    }
    private void clearRect() {
        var rec = bankDrawer.getRectangle();
        canvas.getGraphicsContext2D().clearRect(0, 0, (canvas.getWidth() - rec.getWidth())/2, canvas.getHeight());
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), (canvas.getHeight() - rec.getHeight())/2);
        canvas.getGraphicsContext2D().clearRect(0, canvas.getHeight() - (canvas.getHeight() - rec.getHeight())/2, canvas.getWidth(), (canvas.getHeight() - rec.getHeight())/2);
        canvas.getGraphicsContext2D().clearRect(canvas.getWidth() - (canvas.getWidth() - rec.getWidth())/2, 0, (canvas.getWidth() - rec.getWidth())/2, canvas.getHeight());
    }
    public void simulate() {
        simulationTimeline.play();
    }
    public void stopSimulation() {
        simulationTimeline.stop();
    }
    public void setSpeed(double value) {
        bankDrawer.setSpeed(value);
    }
    public String getReport() {
        return bank.toString();
    }
    public boolean isDone() {
        return bank.getCurrentDuration().compareTo(durationOfSimulation) >= 0;
    }

    private static void generateClerks(Bank bank, Random random, int count, double dailyClerkSalary) {
        for(int i = 0; i < count; i++) {
            String surname = surnames[random.nextInt(0, surnames.length)];
            String name = names[random.nextInt(0, names.length)];
            String patronymic = patronymics[random.nextInt(0, patronymics.length)];
            bank.hire(new Clerk(surname, name, patronymic, dailyClerkSalary, bank.getCurrentDateTime()));
        }
    }
    private static Client getRandomClient(Random random) {
        String surname = surnames[random.nextInt(0, surnames.length)];
        String name = names[random.nextInt(0, names.length)];
        String patronymic = patronymics[random.nextInt(0, patronymics.length)];
        return new Client(surname, name, patronymic);
    }

    public static String serialize(SimulationController simulationController) {
        return gson.toJson(simulationController);
    }
    public static SimulationController deserialize (String json) {
        return gson.fromJson(json, SimulationController.class);
    }
}
