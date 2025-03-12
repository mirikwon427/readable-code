package cleancode.studycafe.tobe;

import cleancode.studycafe.tobe.exception.AppException;
import cleancode.studycafe.tobe.io.StudyCafeIOHandler;
import cleancode.studycafe.tobe.model.order.StudyCafePassOrder;
import cleancode.studycafe.tobe.model.pass.locker.StudyCafeLockerPass;
import cleancode.studycafe.tobe.model.pass.locker.StudyCafeLockerPasses;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;
import cleancode.studycafe.tobe.model.pass.StudyCafePassType;

import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPasses;
import cleancode.studycafe.tobe.provider.LockerPassProvider;
import cleancode.studycafe.tobe.provider.SeatPassProvider;
import java.util.List;
import java.util.Optional;

public class StudyCafePassMachine {

    private final StudyCafeIOHandler ioHandler = new StudyCafeIOHandler();
    private final SeatPassProvider seatPassProvider;
    private final LockerPassProvider lockPassProvider;

  public StudyCafePassMachine(SeatPassProvider seatPassProvider,
      LockerPassProvider lockPassProvider) {
    this.seatPassProvider = seatPassProvider;
    this.lockPassProvider = lockPassProvider;
  }

  public void run() {
        try {
            ioHandler.showWelcomeMessage();
            ioHandler.showAnnouncement();

            StudyCafeSeatPass selectedPass = selectPass();
            Optional<StudyCafeLockerPass> optionalLockerPass = selectLockerPass(selectedPass);
            StudyCafePassOrder passOrder = StudyCafePassOrder.of(
                selectedPass,
                optionalLockerPass.orElse(null)
            );
            ioHandler.showPassOrderSummary(passOrder);

//            if(optionalLockerPass.isPresent()) {
//                outputHandler.showPassOrderSummary(selectedPass, optionalLockerPass.get());
//            } else {
//                outputHandler.showPassOrderSummary(selectedPass, null);
//            }

        } catch (AppException e) {
            ioHandler.showSimpleMessage(e.getMessage());
        } catch (Exception e) {
            ioHandler.showSimpleMessage("알 수 없는 오류가 발생했습니다.");
        }
    }

    private StudyCafeSeatPass selectPass() {
        StudyCafePassType studyCafePassType = ioHandler.askPassTypeSelecting();
        List<StudyCafeSeatPass> passCandidates = findPassCandidatesBy(studyCafePassType);

        return ioHandler.askPassSelecting(passCandidates);
    }

    private List<StudyCafeSeatPass> findPassCandidatesBy(StudyCafePassType studyCafePassType) {
       StudyCafeSeatPasses allPasses = seatPassProvider.getSeatPasseas();
       return allPasses.findPassBy(studyCafePassType);
    }

    private Optional<StudyCafeLockerPass> selectLockerPass(StudyCafeSeatPass selectedPass) {
        if (selectedPass.cannotUseLocker()) {
            return Optional.empty();
        }

        Optional<StudyCafeLockerPass> lockerPassCandidate = findLockerPassCandidateBy(selectedPass);

        if (lockerPassCandidate.isPresent()) {
            StudyCafeLockerPass lockerPass = lockerPassCandidate.get();

            boolean isLockerSelected  = ioHandler.askLockerPass(lockerPass);
            if(isLockerSelected) {
                return Optional.of(lockerPass);
            }
        }

        return Optional.empty();
    }

    private Optional<StudyCafeLockerPass> findLockerPassCandidateBy(StudyCafeSeatPass pass) {
        StudyCafeLockerPasses allLockerPasses = lockPassProvider.getLockerPasseas();

        return allLockerPasses.findLockerPassBy(pass);
    }

}
