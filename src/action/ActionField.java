package action;

import gameRecord.RecordTankActions;
import objectsOnField.*;
import objectsOnField.Void;
import tanks.*;
import tanks.Action;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ActionField {

    volatile private ConcurrentSkipListSet<Integer> pressed = new ConcurrentSkipListSet<>();
    private BattleField battleField;
    private Tank defender;
    private Tank firstAggressor;
    private Tank secondAggressor;

    private JFrame frame;
    private int firstAggressorIdx;
    private int secondAggressorIdx;
    private int mapLevel;

    private List defenderActionsFromRecord;
    private List firstAggressorActionsFromRecord;
    private List secondAggressorActionsFromRecord;
    private List tanksActions;

    private String firstAggressorLocation;
    private int firstAggressorLocationX;
    private int firstAggressorLocationY;

    private String secondAggressorLocation;
    private int secondAggressorLocationX;
    private int secondAggressorLocationY;

    private String defenderLocation;
    private int defenderLocationX;
    private int defenderLocationY;

    private boolean firstAggressorPlayer;
    private boolean secondAggressorPlayer;

    boolean notReplay = true;
    private AbstractObjectOnField eagle;

    private final String imageName = "/image/ImageForObjects.png";
    private final String fileName = "GameRecord.txt";

    private FileOutputStream fos;
    private ObjectOutputStream oos;
    private RecordTankActions recordActions;
    private List recordList;
    private int ifBrokeRecord;
    private List<Bullet> bullets;

    private Image image;
    private List<Tank> tanksList;

    public ActionField() throws Exception {
        try {
            image = ImageIO.read(getClass().getResource(imageName));
        } catch (IOException e) {
            System.out.println("Image not found");
        }
        battleField = new BattleField(image, mapLevel);

        gameOptions();
        frame = new JFrame("BATTLE FIELD, DAY 2");
        frame.setLocation(500, 150);
        frame.setMinimumSize(new Dimension(battleField.getBF_WIDTH(), battleField.getBF_HEIGHT()));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(gameOptions());
        frame.setVisible(true);
        frame.pack();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                pressedButtons(e.getKeyCode());
                pressed.add(e.getKeyCode());
            }

            @Override
            public synchronized void keyReleased(KeyEvent e) {
                pressed.remove(e.getKeyCode());
            }
        });
    }

    private Thread paint() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (isGameEnd() && ifBrokeRecord != tanksList.size()) {
                    frame.repaint();
                    sleep(1000 / 60);
                }
                for(int i = 0; i < 60; i++) {
                    frame.repaint();
                    sleep(1000 / 60);
                }
                try {
                    if(oos != null) {
                        oos.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
                closeOrRepeat();
            }
        });
    }
    private void runTheGame() {
        for(Tank tank : tanksList) {
           tankGameThread(tank).start();
        }
    }

    private void runTheRecordGame() {
        for(int i = 0; i < tanksList.size(); i++) {
            tankRecordGameThread(tanksList.get(i), i).start();
        }
    }

    private boolean isGameEnd() {
        if((!firstAggressor.isDestroyed() || !secondAggressor.isDestroyed()) && !defender.isDestroyed() && !eagle.isDestroyed()) {
            return true;
        } else {
            return false;
        }
    }

    private Thread tankRecordGameThread(final Tank tank, final  int i) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                List list = (List) tanksActions.get(i);
                int j;
                for(int i = 0; i < list.size(); i++) {
                    j = 0;
                    while (j < 1) {
                        tank.setDirection((Direction) list.get(i++));
                        recordProcessAction((Action) list.get(i), tank);
                        sleep(10);
                        j++;
                    }
                }
                ifBrokeRecord++;
            }
        });

    }
    private Thread tankGameThread(final Tank tank) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isGameEnd() && !tank.isDestroyed()) {

                    processAction(tank.setUp(tanksList), tank);
                    sleep(10);
                }
            }
        });
        return thread;
    }

    private void recordProcessAction(Action a, Tank t) {
        if(a == Action.MOVE) {
            processMove(t);
        } else if(a == Action.FIRE && t.isRecharge()) {
            processFire(t.fire());
        }
    }

    private void processAction(Action a, Tank t) {
        if (a == Action.MOVE) {
            processMove(t);
        } else if (a == Action.FIRE && t.isRecharge()) {
            processFire(t.fire());
        }

        t.setPlayerAction(Action.NONE);

        recordActions.write(t.getCrewName(), oos, t.getDirection(), a);
    }


    public void processMove(Tank tank) {
        Direction direction = tank.getDirection();

        String tankQuadrant = getQuadrant(tank.getX(), tank.getY());
        int v = Integer.parseInt(tankQuadrant.split("_")[0]);
        int h = Integer.parseInt(tankQuadrant.split("_")[1]);

        if (direction == Direction.UP) {
            v--;
        } else if (direction == Direction.DOWN) {
            v++;
        } else if (direction == Direction.RIGHT) {
            h++;
        } else if (direction == Direction.LEFT) {
            h--;
        }

        if(checkTank(tank, v, h)) {
            return;
        }

        if (checkLimits(direction, tank)) {
            return;
        }

        if(checkNextQuadrant(v, h)) {
            return;
        }

        int i = 0;
        int step = 1;
        while (i < 64) {
                if (direction == Direction.UP) {
                    tank.updateY(-step);

                } else if (direction == Direction.DOWN) {
                    tank.updateY(step);

                } else if (direction == Direction.LEFT) {
                    tank.updateX(-step);

                } else if (direction == Direction.RIGHT) {
                    tank.updateX(step);
                }

                i += step;
                sleep(tank.getSpeed());
        }
    }
    private boolean checkLimits(Direction direction, Tank tank) {
        if ((direction == Direction.UP && tank.getY() == battleField.getMinRiftField())
                || (direction == Direction.DOWN && tank.getY() >= battleField.getMaxRiftField())
                || (direction == Direction.LEFT && tank.getX() == battleField.getMinRiftField())
                || (direction == Direction.RIGHT && tank.getX() >= battleField.getMaxRiftField())) {
            return true;
        } else return false;
    }

    private boolean checkTank(Tank tank, int y, int x) {
        for(Tank t : tanksList) {
            if(tank != t && Math.abs(x * battleField.getLimitQuadrant() - t.getX()) < 64 && Math.abs(y * battleField.getLimitQuadrant() - t.getY()) < 64 && !t.isDestroyed()){
                return true;
            }
        }
        return false;
    }

    private boolean checkNextQuadrant(int v, int h) {
        AbstractObjectOnField abstractObjectOnField = battleField.scanQuadrant(v, h);
        if (!(abstractObjectOnField instanceof Void) && !abstractObjectOnField.isDestroyed()) {
            return true;
        } else {
            return false;
        }
    }

    public void processFire(final Bullet bullet) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bullets.add(bullet);
                int step = 1;
                int bodyX = bullet.getX();
                int bodyY = bullet.getY();
                while (bullet.getX() < 590 && bullet.getX() > -14 &&
                        bullet.getY() < 590 && bullet.getY() > -14) {

                        if (bullet.getDirection() == Direction.UP) {
                            bullet.updateY(-step);
                            bodyY = bullet.getY();
                        } else if (bullet.getDirection() == Direction.DOWN) {
                            bullet.updateY(step);
                            bodyY = bullet.getY() + 14;

                        } else if (bullet.getDirection() == Direction.LEFT) {
                            bullet.updateX(-step);
                            bodyX = bullet.getX();

                        } else {
                            bullet.updateX(step);
                            bodyX = bullet.getX() + 14;
                        }

                        if (processInterception(bullet, bodyX, bodyY)) {
                            break;
                        }
                        sleep(bullet.getSpeed());
                }
                bullets.remove(bullet);
            }
        }).start();
    }

    private boolean processInterception(Bullet bullet, int bodyX, int bodyY) {
        for(int i = 0; i < bullets.size(); i++) {
            if(bullet != bullets.get(i) && Math.abs(bullet.getX() - bullets.get(i).getX()) < 14 && Math.abs(bullet.getY() - bullets.get(i).getY()) < 14) {
                try {
                    bullet.destroy();
                    bullets.get(i).destroy();
                    return true;
                } finally {
                    bullets.get(i).destroy();
                    bullet.destroy();
                    return true;
                }
            }
        }

        int x = bodyX / battleField.getLimitQuadrant();
        int y = bodyY / battleField.getLimitQuadrant();

        if (x >= 0 && x < 9 && y >= 0 && y < 9) {
            AbstractObjectOnField abstractObjectOnField = battleField.scanQuadrant(y, x);
            if(bullet.getTank() instanceof Tiger) {
                if (!abstractObjectOnField.isDestroyed() && !(abstractObjectOnField instanceof Void) && !(abstractObjectOnField instanceof Water)) {
                    battleField.destroyObject(y, x);
                    bullet.destroy();
                    return true;
                }
            }else  {
                if (!abstractObjectOnField.isDestroyed() && !(abstractObjectOnField instanceof Void) && !(abstractObjectOnField instanceof Water)) {
                    if(abstractObjectOnField instanceof Rock) {
                        bullet.destroy();
                        return true;
                    }else {
                        battleField.destroyObject(y, x);
                        bullet.destroy();
                        return true;
                    }
                }
            }
        }

        for(Tank tank : tanksList) {
            if(!tank.isDestroyed() && Math.abs((bullet.getX() + 7) - (tank.getX() + 32)) < 39 &&
                    Math.abs((bullet.getY() + 7) - (tank.getY() + 32)) < 39 && !bullet.getCrewName().substring(0, 1).equals(tank.getCrewName().substring(0, 1))) {
                tank.destroy();
                bullet.destroy();
                return true;
            }
        }
        return false;
    }

    String getQuadrant(int x, int y) {
        return y / battleField.getLimitQuadrant() + "_" + x / battleField.getLimitQuadrant();
    }

    private void drawBullets(Graphics g) {
        try{
            for (Bullet bullet : bullets) {
                bullet.draw(g);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void pressedButtons(int button) {

            if (button == KeyEvent.VK_A) {
                defender.setPlayerAction(Action.LEFT);
            } else if (button == KeyEvent.VK_D) {
                defender.setPlayerAction(Action.RIGHT);
            } else if (button == KeyEvent.VK_W) {
                defender.setPlayerAction(Action.UP);
            } else if (button == KeyEvent.VK_S) {
                defender.setPlayerAction(Action.DOWN);
            } else if (button == KeyEvent.VK_SPACE) {
                defender.setPlayerAction(Action.FIRE);
            } else if (button == KeyEvent.VK_J) {
                firstAggressor.setPlayerAction(Action.LEFT);
            } else if (button == KeyEvent.VK_L) {
                firstAggressor.setPlayerAction(Action.RIGHT);
            } else if (button == KeyEvent.VK_I) {
                firstAggressor.setPlayerAction(Action.UP);
            } else if (button == KeyEvent.VK_K) {
                firstAggressor.setPlayerAction(Action.DOWN);
            } else if (button == KeyEvent.VK_N) {
                firstAggressor.setPlayerAction(Action.FIRE);
            }
    }
    private JPanel gamePanel() {
        JPanel jGame = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                battleField.draw(g);
                for (Tank tank : tanksList) {
                    tank.draw(g);
                }
                drawBullets(g);
                battleField.drawShrubs(g);

                sleep(1000 / 60);
            }
        };
        frame.setFocusable(false);
        paint().start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isGameEnd()) {
                    if(pressed.size() > 0) {
                        sleep(200);
                        for(int key : pressed) {
                            pressedButtons(key);
                        }
                    }
                }
            }
        }).start();
        return jGame;
    }

    private void actionsFromRecordList() {
        defenderActionsFromRecord = new ArrayList();
        firstAggressorActionsFromRecord = new ArrayList();
        secondAggressorActionsFromRecord = new ArrayList();
        tanksActions = new ArrayList();
        for(int i = 3; i < recordList.size();i++) {
            if (defender.getCrewName().equals(recordList.get(i))) {
                defenderActionsFromRecord.add(recordList.get(++i));
                defenderActionsFromRecord.add(recordList.get(++i));
            } else
                if(firstAggressor.getCrewName().equals(recordList.get(i))) {
                    firstAggressorActionsFromRecord.add(recordList.get(++i));
                    firstAggressorActionsFromRecord.add(recordList.get(++i));
                } else
                    if(secondAggressor.getCrewName().equals(recordList.get(i))) {
                        secondAggressorActionsFromRecord.add(recordList.get(++i));
                        secondAggressorActionsFromRecord.add(recordList.get(++i));
                    }
        }
        tanksActions.add(defenderActionsFromRecord);
        tanksActions.add(firstAggressorActionsFromRecord);
        tanksActions.add(secondAggressorActionsFromRecord);
    }
    private JPanel gameOver() {
        ifBrokeRecord = 0;
        frame.setFocusable(false);
        JPanel gameOver = new JPanel();
        gameOver.setLayout(new GridBagLayout());
        JLabel resultGame;
        if(defender.isDestroyed() || eagle.isDestroyed()) {
            resultGame = new JLabel("You Lose!");
        } else {
            resultGame = new JLabel("You Win!");
        }
        JButton goAgain = new JButton("Play Again");
        goAgain.setForeground(Color.BLACK);
        goAgain.setBackground(new Color(100, 255, 100));

        JButton record = new JButton("Show the game record");
        record.setForeground(Color.BLACK);
        record.setBackground(new Color(100, 255, 100));

        final JButton goOut = new JButton("Exit");
        goOut.setForeground(Color.BLACK);
        goOut.setBackground(new Color(100, 255, 100));

        gameOver.add(resultGame);
        gameOver.add(goAgain);
        gameOver.add(goOut);

        goAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameResult();
            }
        });
        goOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                System.exit(0);
            }
        });

        return gameOver;
    }

    private JPanel gameOptions() {
        firstAggressorIdx = 0;
        secondAggressorIdx = 1;
        mapLevel = 0;
        firstAggressorPlayer = false;
        recordList = new ArrayList();
        recordActions = new RecordTankActions();
        ifBrokeRecord = 0;
        JPanel jTankChoose = new JPanel();
        jTankChoose.setLayout(new GridBagLayout());
        JLabel firstAggressorTank = new JLabel("Choose   First  Aggressor   Tank  : ");

        ButtonGroup firstButtonGroup = new ButtonGroup();
        JPanel firstPanelTanks = new JPanel();
        ActionListener firstRbListener = new RBListener(0);
        String[] tanks = {"BT7", "Tiger"};
        for(int i = 0; i < tanks.length; i++) {
            JRadioButton radioButton = new JRadioButton(tanks[i]);
            radioButton.setActionCommand(String.valueOf(i));
            radioButton.addActionListener(firstRbListener);
            if(i == 0) {
                radioButton.setSelected(true);
            }
            firstButtonGroup.add(radioButton);
            firstPanelTanks.add(radioButton);
        }
        ButtonGroup secondButtonGroup = new ButtonGroup();
        JPanel secondPanelTanks = new JPanel();
        JLabel secondAggressorTank = new JLabel("Choose Second Aggressor Tank : ");
        ActionListener secondRbListener = new RBListener(1);
        for(int i = 1; i >= 0; i--) {
            JRadioButton radioButton = new JRadioButton(tanks[i]);
            radioButton.setActionCommand(String.valueOf(i));
            radioButton.addActionListener(secondRbListener);
            if(i == 1) {
                radioButton.setSelected(true);
            }
            secondButtonGroup.add(radioButton);
            secondPanelTanks.add(radioButton);
        }
        firstPanelTanks.setLayout(new GridLayout(tanks.length / 2, 0, 0, 0));
        firstPanelTanks.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        secondPanelTanks.setLayout(new GridLayout(tanks.length / 2, 0, 0, 0));
        secondPanelTanks.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JLabel aggressorsPlayingLabel = new JLabel("Who Plays For The Aggressors :");
        final JComboBox aggressorsPlayers = new JComboBox();
        aggressorsPlayers.addItem("Computers");
        aggressorsPlayers.addItem("Player and Computer");
        aggressorsPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(aggressorsPlayers.getSelectedIndex() == 0) {
                    firstAggressorPlayer = false;
                } else {
                    firstAggressorPlayer = true;
                }
            }
        });

        final JLabel battleFieldLevel = new JLabel("Choose  Battle  Field  Game  Map : ");
        final JComboBox mapListBox = new JComboBox();
        for(int i = 0; i < battleField.getMapListSize(); i++) {
            mapListBox.addItem("Map" + (i + 1));
        }
        mapListBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapLevel = mapListBox.getSelectedIndex();
            }
        });
        JButton goGame = new JButton("Go Game");
        goGame.setForeground(Color.BLACK);
        goGame.setBackground(new Color(100, 255, 100));

        final JButton record = new JButton("Show  the   last   game  record");
        record.setForeground(Color.BLACK);
        record.setBackground(new Color(100, 255, 100));

        record.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notReplay = false;
                frame.setFocusable(true);
                File file = new File(fileName);
                if(file.exists()) {
                    recordList = recordActions.read(fileName);
                    mapLevel = (int) recordList.get(0);
                    firstAggressorIdx = (int) recordList.get(1);
                    secondAggressorIdx = (int) recordList.get(2);
                    firstAggressorPlayer = false;
                    selectObjects();
                    drawBF();
                    actionsFromRecordList();
                    runTheRecordGame();
                } else {
                    record.setText("Currently record doesn`t exist");
                }
            }
        });

        jTankChoose.add(firstAggressorTank, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jTankChoose.add(firstPanelTanks, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, 0, 0, 0), 0, 0));

        jTankChoose.add(secondAggressorTank, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jTankChoose.add(secondPanelTanks, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, 0, 0, 0), 0, 0));

        jTankChoose.add(aggressorsPlayingLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jTankChoose.add(aggressorsPlayers, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, 0, 0, 0), 0, 0));

        jTankChoose.add(battleFieldLevel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jTankChoose.add(mapListBox, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, 0, 0, 0), 0, 0));

        jTankChoose.add(goGame, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, -50, 0, 0), 0, 0));
        jTankChoose.add(record, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.LINE_START, 0, new Insets(0, -100, 0, 0), 0, 0));

        goGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notReplay = true;
                frame.setFocusable(true);
                selectObjects();
                drawBF();
                try {
                    fos = new FileOutputStream(fileName);
                    oos = new ObjectOutputStream(new BufferedOutputStream(fos));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                recordActions.writeGameData(oos, mapLevel, firstAggressorIdx, secondAggressorIdx);
                runTheGame();
            }
        });
        return jTankChoose;
    }

    private void tanksCoordinates() {
        firstAggressorLocation = battleField.getFirstAggressorLocation();
        firstAggressorLocationX = Integer.parseInt(firstAggressorLocation.split("_")[1]);
        firstAggressorLocationY = Integer.parseInt(firstAggressorLocation.split("_")[0]);

        secondAggressorLocation = battleField.getSecondAggressorLocation();
        secondAggressorLocationX = Integer.parseInt(secondAggressorLocation.split("_")[1]);
        secondAggressorLocationY = Integer.parseInt(secondAggressorLocation.split("_")[0]);

        defenderLocation = battleField.getDefenderLocation();
        defenderLocationX = Integer.parseInt(defenderLocation.split("_")[1]);
        defenderLocationY = Integer.parseInt(defenderLocation.split("_")[0]);
    }

    private void selectObjects() {
        tanksList = new ArrayList<>();
        bullets = new ArrayList<>();
        battleField = new BattleField(image, mapLevel);
        eagle = battleField.getEagleLocation();
        secondAggressorPlayer = false;

        tanksCoordinates();

        defender = new T34(battleField, defenderLocationX, defenderLocationY, Direction.UP, image, "d1");

        if(firstAggressorIdx == 0) {
            firstAggressor = new BT7(battleField,
                    firstAggressorLocationX, firstAggressorLocationY, Direction.DOWN, image, eagle, firstAggressorPlayer, "a1");
        } else if(firstAggressorIdx == 1) {
            firstAggressor = new Tiger(battleField,
                    firstAggressorLocationX, firstAggressorLocationY, Direction.DOWN, image, defender, firstAggressorPlayer, "a1");
        }

        if(secondAggressorIdx == 0) {
            secondAggressor = new BT7(battleField,
                    secondAggressorLocationX, secondAggressorLocationY, Direction.DOWN, image, eagle, secondAggressorPlayer, "a2");
        } else if(secondAggressorIdx == 1) {
            secondAggressor = new Tiger(battleField,
                    secondAggressorLocationX, secondAggressorLocationY, Direction.DOWN, image, defender, secondAggressorPlayer, "a2");
        }

        tanksList.add(defender);
        tanksList.add(firstAggressor);
        tanksList.add(secondAggressor);
    }
    private void drawBF(){
        frame.getContentPane().removeAll();
        frame.getContentPane().add(gamePanel());
        frame.setFocusable(true);
        frame.pack();
    }

    private void closeOrRepeat() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(gameOver());
        frame.pack();
        frame.repaint();
    }

    private void gameResult() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(gameOptions());
        frame.pack();
        frame.repaint();
    }

    private class RBListener implements ActionListener {
        private int idx;
        public RBListener(int idx) {
            this.idx = idx;
        }
        @Override
        public void actionPerformed(ActionEvent e) {

            if(idx == 0) {
               firstAggressorIdx = Integer.parseInt(e.getActionCommand());
            } else {
                secondAggressorIdx = Integer.parseInt(e.getActionCommand());
            }
        }
    }

    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            // ignore
        }
    }

}