package intermediate;

import com.sandwich.koan.Koan;

import static com.sandwich.koan.constant.KoanConstants.__;
import static com.sandwich.util.Assert.assertEquals;

public class AboutInnerClasses {

    interface Ignoreable {
        String ignoreAll();
    }

    class Inner {
        public String doStuff() {
            return "stuff";
        }

        public int returnOuter() {
            return x;
        }
    }

    @Koan
    public void creatingInnerClassInstance() {
        Inner someObject = new Inner();
        assertEquals(someObject.doStuff(), "stuff");
    }

    @Koan
    public void creatingInnerClassInstanceWithOtherSyntax() {
        AboutInnerClasses.Inner someObject = this.new Inner();
        assertEquals(someObject.doStuff(), "stuff");
    }

    private int x = 10;

    @Koan
    public void accessingOuterClassMembers() {
        Inner someObject = new Inner();
        assertEquals(someObject.returnOuter(), 10);
    }

    @Koan
    public void innerClassesInMethods() {
        class MethodInnerClass {
            int oneHundred() {
                return 100;
            }
        }
        assertEquals(new MethodInnerClass().oneHundred(), 100);
    }

    class AnotherInnerClass {
        int thousand() {
            return 1000;
        }

        AnotherInnerClass crazyReturn() {
            class SpecialInnerClass extends AnotherInnerClass {
                int thousand() {
                    return 2000;
                }
            }
            return new SpecialInnerClass();
        }
    }

    @Koan
    public void innerClassesInMethodsThatEscape() {
        AnotherInnerClass ic = new AnotherInnerClass();
        assertEquals(ic.thousand(), 1000);
        AnotherInnerClass theCrazyIC = ic.crazyReturn();
        assertEquals(theCrazyIC.thousand(), 2000);
    }

    int theAnswer() {
        return 42;
    }

    @Koan
    public void creatingAnonymousInnerClasses() {
        AboutInnerClasses anonymous = new AboutInnerClasses() {
            int theAnswer() {
                return 23;
            }
        };
        assertEquals(anonymous.theAnswer(), 23);
    }

    @Koan
    public void creatingAnonymousInnerClassesToImplementInterface() {
        Ignoreable ignoreable = new Ignoreable() {
            public String ignoreAll() {
                return "SomeInterestingString";
            }
        };
        assertEquals(ignoreable.ignoreAll(), "SomeInterestingString");
    }

    @Koan
    public void innerClassAndInheritance() {
        Inner someObject = new Inner();
        assertEquals(someObject instanceof Inner, true);
    }

    class OtherInner extends AboutInnerClasses {
    }

    @Koan
    public void innerClassAndInheritanceOther() {
        OtherInner someObject = new OtherInner();
        assertEquals(someObject instanceof AboutInnerClasses, true);
    }

    static class StaticInnerClass {
        public int importantNumber() {
            return 3;
        }
    }

    @Koan
    public void staticInnerClass() {
        StaticInnerClass someObject = new StaticInnerClass();
        assertEquals(someObject.importantNumber(), 3);
    }

    @Koan
    public void staticInnerClassFullyQualified() {
        AboutInnerClasses.StaticInnerClass someObject = new AboutInnerClasses.StaticInnerClass();
        assertEquals(someObject.importantNumber(), 3);
    }
}
