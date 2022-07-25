import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

import { AuthService } from './auth.service';

class MockRoute {
    navigate(url: string) { return url; }
}

describe('AuthService', () => {
    let service: AuthService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: Router, useClass: MockRoute},
                CookieService
            ],
            imports: [HttpClientTestingModule]
        });
        service = TestBed.inject(AuthService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return false cookie is expired', () => {
        service.addToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjNkZDZjYTJhODFkYzJmZWE4YzM2NDI0MzFlN2UyOTZkMmQ3NWI0NDYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxNTUxNDUwNDU2OTEyNTgwOTYiLCJlbWFpbCI6ImJwYWRoaXlhcjRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ4T01MbWw0NXJCQVgwLVhGTklOdFd3Iiwibm9uY2UiOiJYbEh2MzNGWmRCVEZCYnlhMWFRS0tLaHJUX09adklMNlNpVWw3bEg4LTdvIiwibmFtZSI6IkJoYXJhdCBQYWRoaXlhciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaVJVTDJXcWlZRWxfV054SXN5a0xka055QXFBeDBLTk9ZeTBzaWFtZz1zOTYtYyIsImdpdmVuX25hbWUiOiJCaGFyYXQiLCJmYW1pbHlfbmFtZSI6IlBhZGhpeWFyIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2NDY0OTY1MDEsImV4cCI6MTY0NjUwMDEwMX0.GFXzQVTFESalZWBM7PzUdijhOHTqHzSFrQJx2Qo0tqKa0MAV_yfh_HsACNwjPPKUyTeWchGOBVa6sQP3kubNYQFBM49edNGhznPG8zxceoG0hWYY5fUAR2XmIWocA0_97dty0PLJifwum8WX39-OR0B7klyueLfL_fuyUGkZonwriTMnZYjylMTQpXGwxdQTsspkW3UhOj7i8v6gdrFush9P2hSbMkZIFiBoBNvd_Djg9y5ZSOJH7G7WCrBU1ZNNZlSWQDnj-q-3lIPxWWHtRrdL10mNJamQjJu3AmxWSrCAhOm9McJYARHSIcMPPpHFx4tKgolRsjiRyA5xCLESMQ");
        expect(service.isLoggedIn()).toBeFalsy();
    });

    it('should add token', () => {
        service.addToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjNkZDZjYTJhODFkYzJmZWE4YzM2NDI0MzFlN2UyOTZkMmQ3NWI0NDYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxNTUxNDUwNDU2OTEyNTgwOTYiLCJlbWFpbCI6ImJwYWRoaXlhcjRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ4T01MbWw0NXJCQVgwLVhGTklOdFd3Iiwibm9uY2UiOiJYbEh2MzNGWmRCVEZCYnlhMWFRS0tLaHJUX09adklMNlNpVWw3bEg4LTdvIiwibmFtZSI6IkJoYXJhdCBQYWRoaXlhciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaVJVTDJXcWlZRWxfV054SXN5a0xka055QXFBeDBLTk9ZeTBzaWFtZz1zOTYtYyIsImdpdmVuX25hbWUiOiJCaGFyYXQiLCJmYW1pbHlfbmFtZSI6IlBhZGhpeWFyIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2NDY0OTY1MDEsImV4cCI6MTY0NjUwMDEwMX0.GFXzQVTFESalZWBM7PzUdijhOHTqHzSFrQJx2Qo0tqKa0MAV_yfh_HsACNwjPPKUyTeWchGOBVa6sQP3kubNYQFBM49edNGhznPG8zxceoG0hWYY5fUAR2XmIWocA0_97dty0PLJifwum8WX39-OR0B7klyueLfL_fuyUGkZonwriTMnZYjylMTQpXGwxdQTsspkW3UhOj7i8v6gdrFush9P2hSbMkZIFiBoBNvd_Djg9y5ZSOJH7G7WCrBU1ZNNZlSWQDnj-q-3lIPxWWHtRrdL10mNJamQjJu3AmxWSrCAhOm9McJYARHSIcMPPpHFx4tKgolRsjiRyA5xCLESMQ");
        expect(service.getToken()).toBeDefined();
    });

    it('should remove token on logout', () => {
        service.addToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjNkZDZjYTJhODFkYzJmZWE4YzM2NDI0MzFlN2UyOTZkMmQ3NWI0NDYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxNTUxNDUwNDU2OTEyNTgwOTYiLCJlbWFpbCI6ImJwYWRoaXlhcjRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ4T01MbWw0NXJCQVgwLVhGTklOdFd3Iiwibm9uY2UiOiJYbEh2MzNGWmRCVEZCYnlhMWFRS0tLaHJUX09adklMNlNpVWw3bEg4LTdvIiwibmFtZSI6IkJoYXJhdCBQYWRoaXlhciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaVJVTDJXcWlZRWxfV054SXN5a0xka055QXFBeDBLTk9ZeTBzaWFtZz1zOTYtYyIsImdpdmVuX25hbWUiOiJCaGFyYXQiLCJmYW1pbHlfbmFtZSI6IlBhZGhpeWFyIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2NDY0OTY1MDEsImV4cCI6MTY0NjUwMDEwMX0.GFXzQVTFESalZWBM7PzUdijhOHTqHzSFrQJx2Qo0tqKa0MAV_yfh_HsACNwjPPKUyTeWchGOBVa6sQP3kubNYQFBM49edNGhznPG8zxceoG0hWYY5fUAR2XmIWocA0_97dty0PLJifwum8WX39-OR0B7klyueLfL_fuyUGkZonwriTMnZYjylMTQpXGwxdQTsspkW3UhOj7i8v6gdrFush9P2hSbMkZIFiBoBNvd_Djg9y5ZSOJH7G7WCrBU1ZNNZlSWQDnj-q-3lIPxWWHtRrdL10mNJamQjJu3AmxWSrCAhOm9McJYARHSIcMPPpHFx4tKgolRsjiRyA5xCLESMQ");
        service.logout();
        expect(service.getToken()).toEqual('');
    });
});
