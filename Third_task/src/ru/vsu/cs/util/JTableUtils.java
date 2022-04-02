package ru.vsu.cs.util;

import ru.vsu.cs.novichikhin.Card;
import ru.vsu.cs.novichikhin.SimpleQueue;
import ru.vsu.cs.novichikhin.SimpleStack;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Queue;
import java.util.*;


/**
 * Набор функций для работы с JTable (ввода и отбражения массивов)
 *
 * @author Дмитрий Соломатин (кафедра ПиИТ ФКН ВГУ)
 * @see <a href="http://java-online.ru/swing-jtable.xhtml">http://java-online.ru/swing-jtable.xhtml</a>
 */
public class JTableUtils {

    public static final int DEFAULT_GAP = 6;
    public static final int DEFAULT_PLUSMINUS_BUTTONS_SIZE = 22;
    public static final int DEFAULT_COLUMN_WIDTH = 40;
    public static final int DEFAULT_ROW_HEADER_WIDTH = 40;
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final char DELETE_KEY_CHAR_CODE = 127;
    private static final Border DEFAULT_CELL_BORDER = BorderFactory.createEmptyBorder(0, 3, 0, 3);
    private static final Border DEFAULT_RENDERER_CELL_BORDER = DEFAULT_CELL_BORDER;
    private static final Border DEFAULT_EDITOR_CELL_BORDER = BorderFactory.createEmptyBorder(0, 3, 0, 2);

    private static final Map<JTable, Integer> tableColumnWidths = new HashMap<>();

    private static final NumberFormat defaultNumberFormat = NumberFormat.getInstance(Locale.ROOT);

    private static double parseDouble(String s) throws NumberFormatException {
        try {
            return defaultNumberFormat.parse(s).doubleValue();
        } catch (ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }


    private static <T extends JComponent> T setFixedSize(T comp, int width, int height) {
        Dimension d = new Dimension(width, height);
        comp.setMaximumSize(d);
        comp.setMinimumSize(d);
        comp.setPreferredSize(d);
        comp.setSize(d);
        return comp;
    }

    private static JButton createPlusMinusButton(String text, int size) {
        JButton button = new JButton(text);
        setFixedSize(button, size, size).setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setFocusPainted(false);
        return button;
    }

    private static int getColumnWidth(JTable table) {
        Integer columnWidth = tableColumnWidths.get(table);
        if (columnWidth == null) {
            if (table.getColumnCount() > 0) {
                columnWidth = table.getWidth() / table.getColumnCount();
            } else {
                columnWidth = DEFAULT_COLUMN_WIDTH;
            }
        }
        return columnWidth;
    }

    private static void recalculateJTableSize(JTable table) {
        int width = getColumnWidth(table) * table.getColumnCount();
        int height = 0, rowCount = table.getRowCount();
        for (int r = 0; r < rowCount; r++)
            height += table.getRowHeight(height);
        setFixedSize(table, width, height);

        if (table.getParent() instanceof JViewport && table.getParent().getParent() instanceof JScrollPane scrollPane) {
            if (scrollPane.getRowHeader() != null) {
                Component rowHeaderView = scrollPane.getRowHeader().getView();
                if (rowHeaderView instanceof JList) {
                    ((JList) rowHeaderView).setFixedCellHeight(table.getRowHeight());
                }
                scrollPane.getRowHeader().repaint();
            }
        }
    }

    private static void addRowHeader(JTable table, TableModel tableModel, JScrollPane scrollPane) {
        final class RowHeaderRenderer extends JLabel implements ListCellRenderer {
            RowHeaderRenderer() {
                JTableHeader header = table.getTableHeader();
                setOpaque(true);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setHorizontalAlignment(CENTER);
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }

            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                setText(String.format("%d", index + 1));
                return this;
            }
        }

        ListModel lm = new AbstractListModel() {
            @Override
            public int getSize() {
                return tableModel.getRowCount();
            }

            @Override
            public Object getElementAt(int index) {
                return String.format("[%d]", index);
            }
        };

        JList rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(DEFAULT_ROW_HEADER_WIDTH);
        rowHeader.setFixedCellHeight(
                table.getRowHeight()// + table.getRowMargin()// + table.getIntercellSpacing().height
        );
        rowHeader.setCellRenderer(new RowHeaderRenderer());

        scrollPane.setRowHeaderView(rowHeader);
        scrollPane.getRowHeader().getView().setBackground(scrollPane.getColumnHeader().getBackground());
    }

    /**
     * Настройка JTable для работы с массивами
     *
     * @param table                  компонент JTable
     * @param defaultColWidth        ширина столбцов (ячеек)
     * @param showRowsIndexes        показывать индексы строк
     * @param showColsIndexes        показывать индексы столбцов
     * @param changeRowsCountButtons добавить кнопки для добавления/удаления строк
     * @param changeColsCountButtons добавить кнопки для добавления/удаления столбцов
     * @param changeButtonsSize      размер кнопок для изменения количества строк и столбцов
     * @param changeButtonsMargin    отступ кнопок от таблицы (а также расстояние между кнопками)
     */
    public static void initJTableForArray(
            JTable table, int defaultColWidth,
            boolean showRowsIndexes, boolean showColsIndexes,
            boolean changeRowsCountButtons, boolean changeColsCountButtons,
            int changeButtonsSize, int changeButtonsMargin
    ) {
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        if (!showColsIndexes && table.getTableHeader() != null) {
            table.getTableHeader().setPreferredSize(new Dimension(0, 0));
        }
        table.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(false);
        table.setDragEnabled(false);
        //table.setCursor(Cursor.getDefaultCursor());
        table.putClientProperty("terminateEditOnFocusLost", true);

        DefaultTableModel tableModel = new DefaultTableModel(0, 0) {
            @Override
            public String getColumnName(int index) {
                String[] columnName = new String[]{"Name", "Suit"};
                return String.format("%s", columnName[index]);
            }
        };
        table.setModel(tableModel);
        tableColumnWidths.put(table, defaultColWidth);
        recalculateJTableSize(table);

        if (table.getParent() instanceof JViewport && table.getParent().getParent() instanceof JScrollPane scrollPane) {
            if (changeRowsCountButtons || changeColsCountButtons) {
                List<Component> linkedComponents = new ArrayList<>();

                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

                BorderLayout borderLayout = new BorderLayout(changeButtonsMargin, changeButtonsMargin);
                FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);

                JPanel panel = new JPanel(borderLayout);
                panel.setBackground(TRANSPARENT);

                if (changeColsCountButtons) {
                    JPanel topPanel = new JPanel(flowLayout);
                    topPanel.setBackground(TRANSPARENT);
                    if (changeRowsCountButtons) {
                        topPanel.add(setFixedSize(new Box.Filler(null, null, null), changeButtonsSize + changeButtonsMargin, changeButtonsSize));
                    }
                    JButton minusButton = createPlusMinusButton("\u2013", changeButtonsSize);
                    minusButton.setName(table.getName() + "-minusColumnButton");
                    minusButton.addActionListener((ActionEvent evt) -> {
                        tableModel.setColumnCount(tableModel.getColumnCount() > 0 ? tableModel.getColumnCount() - 1 : 0);
                        recalculateJTableSize(table);
                    });
                    topPanel.add(minusButton);
                    linkedComponents.add(minusButton);
                    topPanel.add(setFixedSize(new Box.Filler(null, null, null), changeButtonsMargin, changeButtonsSize));
                    JButton plusButton = createPlusMinusButton("+", changeButtonsSize);
                    plusButton.setName(table.getName() + "-plusColumnButton");
                    plusButton.addActionListener((ActionEvent evt) -> {
                        tableModel.addColumn(String.format("[%d]", tableModel.getColumnCount()));
                        recalculateJTableSize(table);
                    });
                    topPanel.add(plusButton);
                    linkedComponents.add(plusButton);

                    panel.add(topPanel, BorderLayout.NORTH);
                }
                if (changeRowsCountButtons) {
                    JPanel leftPanel = setFixedSize(new JPanel(flowLayout), changeButtonsSize, changeButtonsSize);
                    leftPanel.setBackground(TRANSPARENT);
                    JButton minusButton = createPlusMinusButton("\u2013", changeButtonsSize);
                    minusButton.setName(table.getName() + "-minusRowButton");
                    minusButton.addActionListener((ActionEvent evt) -> {
                        if (tableModel.getRowCount() > 0) {
                            tableModel.removeRow(tableModel.getRowCount() - 1);
                            recalculateJTableSize(table);
                        }
                    });
                    leftPanel.add(minusButton);
                    linkedComponents.add(minusButton);
                    leftPanel.add(setFixedSize(new Box.Filler(null, null, null), changeButtonsSize, changeButtonsMargin));
                    JButton plusButton = createPlusMinusButton("+", changeButtonsSize);
                    plusButton.setName(table.getName() + "-plusRowButton");
                    plusButton.addActionListener((ActionEvent evt) -> {
                        tableModel.setRowCount(tableModel.getRowCount() + 1);
                        recalculateJTableSize(table);
                    });
                    leftPanel.add(plusButton);
                    linkedComponents.add(plusButton);

                    panel.add(leftPanel, BorderLayout.WEST);
                }
                table.setPreferredScrollableViewportSize(null);
                JScrollPane newScrollPane = new JScrollPane(table);
                newScrollPane.setBackground(scrollPane.getBackground());
                newScrollPane.setBorder(scrollPane.getBorder());
                scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                panel.add(newScrollPane, BorderLayout.CENTER);

                scrollPane.getViewport().removeAll();
                scrollPane.add(panel);
                scrollPane.getViewport().add(panel);

                // привязываем обработчик событий, который активирует и дективирует зависимые
                // компоненты (кнопки) в зависимости от состояния table
                table.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if ("enabled".equals(evt.getPropertyName())) {
                        boolean enabled = (boolean) evt.getNewValue();
                        linkedComponents.forEach((comp) -> comp.setEnabled(enabled));
                        if (!enabled) {
                            table.clearSelection();
                        }
                    }
                });
                linkedComponents.forEach((comp) -> comp.setEnabled(table.isEnabled()));

                // иначе определенные проблемы с прозрачностью panel возникают
                scrollPane.setVisible(false);
                scrollPane.setVisible(true);

                scrollPane = newScrollPane;
            }

            // привязываем отбработчик событий, который снимает выделение,
            // а также обработчик событий, который будет изменять размер таблицы при изменении высоты строки
            table.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                if ("enabled".equals(evt.getPropertyName())) {
                    boolean enabled = (boolean) evt.getNewValue();
                    if (!enabled) {
                        table.clearSelection();
                    }
                } else if ("rowHeight".equals(evt.getPropertyName())) {
                    recalculateJTableSize(table);
                }
            });

            // привязываем обработчик событий, который очищает выделенные ячейки по клавише delete
            table.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyChar() == DELETE_KEY_CHAR_CODE) {
                        for (int r : table.getSelectedRows()) {
                            for (int c : table.getSelectedColumns()) {
                                table.setValueAt(null, r, c);
                            }
                        }
                    }
                }
            });

            // устанавливаем CellRenderer, который меняет выравнивание в ячейках в зависимости
            // от содержимого (целые числа - выравнивание вправо, иначе - влево) + красивые отступы
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (comp instanceof JLabel label) {
                        label.setHorizontalAlignment(CENTER);
                        label.setBorder(DEFAULT_RENDERER_CELL_BORDER);
                    }
                    return comp;
                }
            });
            // устанавливаем CellEditor, который меняет выравнивание в ячейках в зависимости
            // от содержимого (целые числа - выравнивание вправо, иначе - влево) + красивые отступы
            table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
                @Override
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                    if (comp instanceof JTextField textField) {
                        textField.setHorizontalAlignment((value == null || value.toString().matches("|-?\\d+")) ? SwingConstants.RIGHT : SwingConstants.LEFT);
                        textField.setBorder(DEFAULT_EDITOR_CELL_BORDER);
                        textField.selectAll();  // чтобы при начале печати перезаписывать текст
                    }
                    return comp;
                }
            });

            if (showRowsIndexes) {
                addRowHeader(table, tableModel, scrollPane);
            }
        }
    }

    /**
     * Аналогичен {@link #initJTableForArray(javax.swing.JTable, int, boolean, boolean, boolean, boolean, int, int) }.
     * {@code changeButtonsSize} принимает значение {@link #DEFAULT_PLUSMINUS_BUTTONS_SIZE}.
     * {@code changeButtonsMargin} принимает значение {@link #DEFAULT_GAP}.
     *
     * @see #initJTableForArray(javax.swing.JTable, int, boolean, boolean, boolean, boolean, int, int)
     */
    public static void initJTableForArray(
            JTable table, int defaultColWidth,
            boolean showRowsIndexes, boolean showColsIndexes,
            boolean changeRowsCountButtons, boolean changeColsCountButtons
    ) {
        initJTableForArray(
                table, defaultColWidth,
                showRowsIndexes, showColsIndexes, changeRowsCountButtons, changeColsCountButtons,
                22, DEFAULT_GAP
        );
    }

    /**
     * Запись данных из массива (одномерного или двухмерного) в JTable
     * (основная реализация, закрытый метод, используется в остальных writeArrayToJTable)
     */
    private static void writeArrayToJTable(JTable table, Object array, String itemFormat) {
        if (!array.getClass().isArray()) {
            return;
        }
        if (!(table.getModel() instanceof DefaultTableModel tableModel)) {
            return;
        }

        tableColumnWidths.put(table, getColumnWidth(table));

        if (itemFormat == null || itemFormat.length() == 0) {
            itemFormat = "%s";
        }
        int rank = 1;
        int len1 = Array.getLength(array), len2 = -1;
        if (len1 > 0) {
            for (int i = 0; i < len1; i++) {
                Object item = Array.get(array, i);
                if (item != null && item.getClass().isArray()) {
                    rank = 2;
                    len2 = Math.max(Array.getLength(item), len2);
                }
            }
        }
        tableModel.setRowCount(rank == 1 ? 1 : len1);
        tableModel.setColumnCount(rank == 1 ? len1 : len2);
        for (int i = 0; i < len1; i++) {
            if (rank == 1) {
                tableModel.setValueAt(String.format(itemFormat, Array.get(array, i)), 0, i);
            } else {
                Object line = Array.get(array, i);
                if (line != null) {
                    if (line.getClass().isArray()) {
                        int lineLen = Array.getLength(line);
                        for (int j = 0; j < lineLen; j++) {
                            tableModel.setValueAt(String.format(itemFormat, Array.get(line, j)), i, j);
                        }
                    } else {
                        tableModel.setValueAt(String.format(itemFormat, Array.get(array, i)), 0, i);
                    }
                }
            }
        }
        recalculateJTableSize(table);
    }

    public static void writeArrayToJTable(JTable table, Queue<Card> queue) {
        String[][] array = new String[queue.size()][2];
        int i = 0;

        while (queue.size() != 0) {
            Card card = queue.poll();
            array[i][0] = card.getName();
            array[i][1] = card.getSuit();
            i++;
        }
        writeArrayToJTable(table, array, "%s");
    }

    public static void writeArrayToJTable(JTable table, Stack<Card> stack) {
        String[][] array = new String[stack.size()][2];
        int i = 0;

        while (stack.size() != 0) {
            Card card = stack.pop();
            array[i][0] = card.getName();
            array[i][1] = card.getSuit();
            i++;
        }
        writeArrayToJTable(table, array, "%s");
    }

    public static void writeArrayToJTable(JTable table, SimpleQueue<Card> queue) {
        String[][] array = new String[queue.count()][2];
        int i = 0;

        while (queue.count() != 0) {
            Card card = queue.poll();
            array[i][0] = card.getName();
            array[i][1] = card.getSuit();
            i++;
        }
        writeArrayToJTable(table, array, "%s");
    }

    public static void writeArrayToJTable(JTable table, SimpleStack<Card> stack) {
        String[][] array = new String[stack.count()][2];
        int i = 0;

        while (stack.count() != 0) {
            Card card = stack.pop();
            array[i][0] = card.getName();
            array[i][1] = card.getSuit();
            i++;
        }
        writeArrayToJTable(table, array, "%s");
    }
}
