package ru.job4j.ood.isp.menu;

import java.util.*;

public class SimpleMenu implements Menu {

    private final List<MenuItem> rootElements = new ArrayList<>();

    @Override
    public boolean add(String parentName, String childName, ActionDelegate actionDelegate) {
        boolean result;
        if (findItem(parentName).isPresent() || findItem(childName).isPresent()) {
            rootElements.add(new SimpleMenuItem(parentName, actionDelegate));
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public Optional<MenuItemInfo> select(String itemName) {
        MenuItemInfo menuItemInfo = null;
        Optional<ItemInfo> itemInfoOptional = findItem(itemName);
        if (itemInfoOptional.isPresent()) {
            ItemInfo itemInfo = itemInfoOptional.get();
            menuItemInfo = new MenuItemInfo(itemInfo.getMenuItem(), itemInfo.getNumber());
        }
        assert menuItemInfo != null;
        return Optional.of(menuItemInfo);
    }

    @Override
    public Iterator<MenuItemInfo> iterator() {
        DFSIterator dfsIterator = new DFSIterator();
        return null;
    }

    private Optional<ItemInfo> findItem(String name) {
        DFSIterator dfsIterator = new DFSIterator();
        ItemInfo itemInfo = null;
        while (dfsIterator.hasNext()) {
            if (dfsIterator.next().getMenuItem().getName().equals(name)) {
                itemInfo = dfsIterator.next();
            }
        }
        assert itemInfo != null;
        return Optional.of(itemInfo);
    }

    private static class SimpleMenuItem implements MenuItem {

        private String name;
        private List<MenuItem> children = new ArrayList<>();
        private ActionDelegate actionDelegate;

        public SimpleMenuItem(String name, ActionDelegate actionDelegate) {
            this.name = name;
            this.actionDelegate = actionDelegate;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<MenuItem> getChildren() {
            return children;
        }

        @Override
        public ActionDelegate getActionDelegate() {
            return actionDelegate;
        }
    }

    private class DFSIterator implements Iterator<ItemInfo> {

        private Deque<MenuItem> stack = new LinkedList<>();

        private Deque<String> numbers = new LinkedList<>();

        DFSIterator() {
            int number = 1;
            for (MenuItem item : rootElements) {
                stack.addLast(item);
                numbers.addLast(String.valueOf(number++).concat("."));
            }
        }

        public Deque<MenuItem> getStack() {
            return stack;
        }

        public Deque<String> getNumbers() {
            return numbers;
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public ItemInfo next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            MenuItem current = stack.removeFirst();
            String lastNumber = numbers.removeFirst();
            List<MenuItem> children = current.getChildren();
            int currentNumber = children.size();
            for (var i = children.listIterator(children.size()); i.hasPrevious();) {
                stack.addFirst(i.previous());
                numbers.addFirst(lastNumber.concat(String.valueOf(currentNumber--)).concat("."));
            }
            return new ItemInfo(current, lastNumber);
        }
    }

    private class ItemInfo {
        private MenuItem menuItem;
        private String number;

        public ItemInfo(MenuItem menuItem, String number) {
            this.menuItem = menuItem;
            this.number = number;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public String getNumber() {
            return number;
        }
    }
}