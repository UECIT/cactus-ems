import {Coordinates, Options, QuestionExtension} from ".";

export class QuestionnaireResponse {
  questionnaireId: string;
  question: string;
  questionId: string;
  response: Options;
  questionType: string;
  responseString: string;
  responseInteger: number;
  responseDecimal: number;
  responseBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentType: string;
  responseCoordinates: Coordinates;
  extension: QuestionExtension;
}