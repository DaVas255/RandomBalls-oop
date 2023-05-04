import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

class Ball {
    int x, y, d;
    Color color;

    Ball(int x, int y, int d, Color color) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.color = color;
    }

    void paint(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, d, d);
        g.setColor(Color.black);
        g.drawOval(x, y, d, d);
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class LeaderboardWindow extends JFrame {
    JTable table;

    public LeaderboardWindow() {
        super("List of leaders");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        String[] columns = {"Name", "Score"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);
        pack();
        setLocationRelativeTo(null);
    }

    public void addRow(String name, int score) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{name, score});
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.DESCENDING)));
        table.setRowSorter(sorter);
    }


}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class App extends JFrame {
    JButton button1, button2, button3, button4;
    final int WINDOW_WIDTH = 650;
    final int WINDOW_HEIGHT = 650;

    final Color[] COLORS = {Color.red, Color.green, Color.blue, Color.gray, Color.orange, Color.pink};
    int showDelay = 300;
    int counter = 0;

    ArrayList<Ball> balls = new ArrayList<>();
    Random random = new Random();

    private GameWindow gameWindow;

    class GameWindow extends JFrame {

        private final String playerName;
        public GameWindow(String playerName) {
            this.playerName = playerName;
            setTitle("Random Balls Game");
            initGameWindow();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(650, 650);
            setLocationRelativeTo(null);
            setResizable(false);
        }

        private void initGameWindow() {
            Canvas canvas = new Canvas();
            canvas.setOpaque(false);
            ImageIcon backgroundImage = new ImageIcon(getClass().getResource("./1.jpg"));
            JLabel background = new JLabel(backgroundImage);
            background.setLayout(new BorderLayout());
            setContentPane(background);
            canvas.setPreferredSize(new Dimension(650, 650));

            JLabel counterLabel = new JLabel("Score: " + counter);
            counterLabel.setForeground(Color.WHITE);
            counterLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            background.add(counterLabel, BorderLayout.NORTH);

            add(canvas);
            pack();
            setLocationRelativeTo(null);
            setResizable(false);
            canvas.setVisible(true);

            Timer timer = new Timer(showDelay, e -> {
                addBall();
                if (balls.size() >= 8) {
                    System.out.println("Game Over: " + counter);
                    ((Timer) e.getSource()).stop();
                    return;
                }
                if (counter % 10 == 0 && showDelay > 100) {
                    showDelay -= 100;
                }
                canvas.repaint();
            });
            timer.start();

            canvas.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    gameWindow.deleteBall(e.getX(), e.getY());
                    counter++;
                    counterLabel.setText("Score: " + counter);
                    canvas.repaint();
                }
            });
        }

        void clear() {
            balls.clear();
            counter = 0;
            showDelay = 300;
        }

        void deleteBall(int x, int y) {
            for (int i = balls.size() - 1; i > -1; i--) {
                double dx = balls.get(i).x + balls.get(i).d/2 - x;
                double dy = balls.get(i).y  + balls.get(i).d/2 - y;
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d < (double) balls.get(i).d /2) {
                    balls.remove(i);
                    break;
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        class Canvas extends JPanel {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (Ball ball : balls) {
                    ball.paint(g);
                }
            }
        }

        @Override
        public void dispose() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Random Balls/scores.txt", true))) {
                writer.write(playerName + "," + counter + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.dispose();
            App.this.setVisible(true);
        }
    }

    public App() {
        setTitle("Random Balls");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 10, 10);
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("./1.jpg"));
        JLabel background = new JLabel(backgroundImage);
        background.setLayout(new BorderLayout());
        setContentPane(background);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        button1 = new JButton("New game");
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        button1.setBackground(Color.darkGray);
        button1.setForeground(Color.lightGray);
        button1.setBorder(BorderFactory.createEmptyBorder());
        button1.setPreferredSize(new Dimension(200, 40));
        button1.setFocusPainted(false);
        gbc.gridy++;
        panel.add(button1, gbc);

        button1.addActionListener(e -> {
            String playerName = JOptionPane.showInputDialog("Введите ваше имя:");
            if (playerName == null) {
                return;
            }
            if (gameWindow != null) {
                gameWindow.clear();
            }
            gameWindow = new GameWindow(playerName);
            setVisible(false);
            gameWindow.setVisible(true);
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        button2 = new JButton("List of leaders");
        button2.setBackground(Color.darkGray);
        button2.setPreferredSize(new Dimension(200, 40));
        button2.setForeground(Color.lightGray);
        button2.setBorder(BorderFactory.createEmptyBorder());
        button2.setFocusPainted(false);
        gbc.gridy++;
        panel.add(button2, gbc);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeaderboardWindow leaderboard = new LeaderboardWindow();
                try (Scanner scanner = new Scanner(new File("Random Balls/scores.txt"))) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(",");
                        leaderboard.addRow(parts[0], Integer.parseInt(parts[1]));
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                leaderboard.setVisible(true);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        button3 = new JButton("How to play?");
        button3.setBackground(Color.darkGray);
        button3.setPreferredSize(new Dimension(200, 40));
        button3.setForeground(Color.lightGray);
        button3.setBorder(BorderFactory.createEmptyBorder());
        button3.setFocusPainted(false);
        gbc.gridy++;
        panel.add(button3, gbc);

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Создаем новое окно
                JFrame infoFrame = new JFrame("How to play");
                infoFrame.setSize(400, 400);
                infoFrame.setLocationRelativeTo(null);
                infoFrame.getContentPane().setBackground(Color.GRAY);
                infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ImageIcon icon = new ImageIcon(getClass().getResource("./s.jpg"));
                Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                JLabel infoLabel = new JLabel("<html><h1>Random Balls</h1><p>1)Every certain time, a ball appears in an unknown place.</p><br>" +
                        "<p>2)You have a certain amount of time to kill this ball.</p><br>" +
                        "<p>If you think for a long time or don't hit the ball, you lose.</p><br>" +
                        "<p>The further you go, the faster the game goes</p></html>",  scaledIcon, JLabel.CENTER);
                infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                infoFrame.getContentPane().add(infoLabel, BorderLayout.CENTER);
                infoFrame.setVisible(true);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        button4 = new JButton("Exit");
        button4.setBackground(Color.RED);
        button4.setPreferredSize(new Dimension(200, 20));
        button4.setForeground(Color.lightGray);
        button4.setBorder(BorderFactory.createEmptyBorder());
        button4.setFocusPainted(false);
        gbc.gridy++;
        panel.add(button4, gbc);
        button4.addActionListener(e -> System.exit(0));

        getContentPane().add(panel, BorderLayout.CENTER);
        setSize(650, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void addBall() {
        int d = random.nextInt(20) + 60;
        int x = random.nextInt(WINDOW_WIDTH - d);
        int y = random.nextInt(WINDOW_HEIGHT - d);
        Color color = COLORS[random.nextInt(COLORS.length)];
        balls.add(new Ball(x, y, d, color));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        new App();
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////