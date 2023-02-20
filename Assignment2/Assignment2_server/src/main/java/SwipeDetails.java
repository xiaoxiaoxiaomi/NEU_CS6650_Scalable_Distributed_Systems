public class SwipeDetails {

  private String leftOrRight;
  private String swiper;
  private String swipee;
  private String comment;

  public SwipeDetails(String leftOrRight, String swiper, String swipee, String comment) {
    this.leftOrRight = leftOrRight;
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  public String getLeftOrRight() {
    return leftOrRight;
  }

  public String getSwiper() {
    return swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public String getComment() {
    return comment;
  }

  public void setLeftOrRight(String leftOrRight) {
    this.leftOrRight = leftOrRight;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
