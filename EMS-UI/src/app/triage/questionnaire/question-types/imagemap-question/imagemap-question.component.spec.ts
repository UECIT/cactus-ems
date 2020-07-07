import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImagemapQuestionComponent } from './imagemap-question.component';

describe('ImagemapQuestionComponent', () => {
  let component: ImagemapQuestionComponent;
  let fixture: ComponentFixture<ImagemapQuestionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImagemapQuestionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImagemapQuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
