package zyz.free.service.leecode.solution;

class Solution2 {


    public static void main(String[] args) {

        ListNode a1 = new ListNode(5);
        ListNode a2 = new ListNode(4, a1);
        ListNode a3 = new ListNode(2, a2);

        ListNode b1 = new ListNode(4);
        ListNode b2 = new ListNode(6, b1);
        ListNode b3 = new ListNode(5, b2);

        Solution2 solution2 = new Solution2();

        ListNode listNode = solution2.addTwoNumbers(a3, b3);


    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 是否需要进位
        int carry = 0;
        ListNode headNode = null;
        ListNode tallNode = null;

        while (l1 != null || l2 != null) {
            int count = 0;
            if (l1 != null) {
                count = count + l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                count = count + l2.val;
                l2 = l2.next;
            }
            count = count + carry;

            carry = count / 10;
            int val = count % 10;


            ListNode currNode = new ListNode(val, null);
            if (headNode == null) {
                headNode = tallNode = currNode;
                continue;
            }
            tallNode.next = currNode;
            tallNode = currNode;

        }
        if (carry == 1) {
            ListNode currNode = new ListNode(carry, null);
            tallNode.next = currNode;
            tallNode = currNode;
        }



        return headNode;
    }


    /**
     * Definition for singly-linked list.
     */
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}



