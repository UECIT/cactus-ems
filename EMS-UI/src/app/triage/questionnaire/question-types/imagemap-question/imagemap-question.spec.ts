import { TriageQuestion, QuestionResponse } from './../../../../model/questionnaire';
import { MaterialModule } from './../../../../material.module';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ImagemapQuestionComponent } from './imagemap-question.component';
import { Component, Input, DebugElement, Predicate, Directive } from "@angular/core";
import { CdssService } from 'src/app/service';
import { AnswerService } from 'src/app/service/answer.service';
import { asyncData } from 'src/app/testing/async-observable-helpers';
import { By } from '@angular/platform-browser';

@Directive({selector: 'app-error-display'})
class ErrorDisplayStub {
  @Input('errorObject')
  public errorObject: any;
}

let comp: ImagemapQuestionComponent;
let fixture: ComponentFixture<ImagemapQuestionComponent>;
let page: Page;

class Page {

    get error() {
        const element = fixture.debugElement.query(By.directive(ErrorDisplayStub));
        return element.injector.get(ErrorDisplayStub) as ErrorDisplayStub;
    }
    get image() {return this.query<HTMLImageElement>(By.css('#image'));}
    get imageSelection() {return this.query<HTMLParagraphElement>(By.css('#imageSelection'));}

    private query<T>(by: Predicate<DebugElement>): T {
        return fixture.debugElement.query(by).nativeElement;
    } 
}

describe('ImagemapQuestionComponent', () => {

    let cdssServiceSpy: {getImage: jasmine.Spy};
    let answerServiceSpy: {selectAnswer: jasmine.Spy};
    let eventListener: jasmine.Spy;
    let readFileSpy: jasmine.Spy;

    beforeEach(() => {
        cdssServiceSpy = jasmine.createSpyObj('CdssService', ['getImage']);
        answerServiceSpy = jasmine.createSpyObj('AnswerService', ['selectAnswer']);

        eventListener = jasmine.createSpy();
        readFileSpy = jasmine.createSpy();
        var dummyFileReader = { addEventListener: eventListener, readAsDataURL: readFileSpy};
        spyOn(window as any, 'FileReader').and.returnValue(dummyFileReader);

        TestBed.configureTestingModule({
            imports: [MaterialModule],
            declarations: [ImagemapQuestionComponent, ErrorDisplayStub],
            providers: [
                {provide: CdssService, useValue: cdssServiceSpy},
                {provide: AnswerService, useValue: answerServiceSpy}
            ]
        });
        fixture = TestBed.createComponent(ImagemapQuestionComponent);
        comp = fixture.componentInstance;
        page = new Page();
    });

    it('should load image', fakeAsync(() => {
        let question = new TriageQuestion();
        question.question = "Some markdown with embedded image![img.png](img.png)"
        comp.triageQuestion = question;
        comp.cdssSupplierId = 4;
        let blob = testBlob();
        cdssServiceSpy.getImage.and.returnValue(Promise.resolve(blob));

        fixture.detectChanges(); //trigger onInit
        tick();

        expect(cdssServiceSpy.getImage).toHaveBeenCalledWith(4, "img.png");
        expect(eventListener.calls.mostRecent().args[0]).toEqual('load');
        expect(readFileSpy).toHaveBeenCalledWith(blob);

        let eventListenerLoadHandler = eventListener.calls.mostRecent().args[1]; 
        let event = { target : { result : 'decoded image'}};
        eventListenerLoadHandler(event); //fire the event listener with the mock result manually.
        fixture.detectChanges();
        
        expect(page.image.getAttribute("src")).toEqual("decoded image");
    }));

    it('should handle clicking on image', () => {
        let clickEvent = {
            offsetX: 4,
            offsetY: 133
        };
        let question = new TriageQuestion();
        comp.answerSelected = [];

        comp.mouseClickOnImage(clickEvent, question);

        let expected = question;
        expected.responseCoordinates = {x: 4, y: 133};
        let qr = new QuestionResponse();
        qr.triageQuestion = expected;
        expect(answerServiceSpy.selectAnswer).toHaveBeenCalledWith([qr]);
        expect(comp.selectedCoordinates).toEqual(expected.responseCoordinates);
    });

    it('should not handle clicking on image when disabled', () => {
        comp.disabled = true;

        comp.mouseClickOnImage({}, new TriageQuestion());

        expect(answerServiceSpy.selectAnswer).not.toHaveBeenCalled();
        expect(comp.selectedCoordinates).toBeUndefined();
    });

    it('should display error when image fails to load', fakeAsync(() => {
        let question = new TriageQuestion();
        question.question = "Some markdown with embedded image![img.png](img.png)"
        comp.triageQuestion = question;
        comp.cdssSupplierId = 4;
        cdssServiceSpy.getImage.and.returnValue(Promise.reject("The error"));

        fixture.detectChanges(); //trigger onInit
        tick();
        fixture.detectChanges();

        expect(readFileSpy).not.toHaveBeenCalled();
        expect(page.error.errorObject).toEqual("The error");   
    }));

    function testBlob() {
        let testImageSrc = "not.a.real.place";
        testImageSrc
        let bytes = new Uint8Array(testImageSrc.length / 2);

        for (var i = 0; i < testImageSrc.length; i += 2) {
            bytes[i / 2] = parseInt(testImageSrc.substring(i, i+2), 16);
        }
        return new Blob([bytes]);
    }
})