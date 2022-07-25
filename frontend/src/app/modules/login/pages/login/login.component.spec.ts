import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { LoginComponent } from './login.component';
import { CookieService } from 'ngx-cookie-service';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/data/service/auth-service/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let route: ActivatedRoute;
    let authService: AuthService;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [LoginComponent],
            imports: [RouterTestingModule.withRoutes([]), HttpClientTestingModule],
            providers: [CookieService]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        route = TestBed.inject(ActivatedRoute);
        authService = TestBed.inject(AuthService);
    });

    afterEach(() => {
        authService.logout
    })

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should add token in cookie from url', () => {
        const routerSpy = spyOn(route.snapshot.queryParamMap, 'get');
        routerSpy.and.returnValue('dummyToken');
        component.ngOnInit();
        expect(authService.getToken()).toBeDefined();
        expect(route.snapshot.queryParamMap.get).toHaveBeenCalled();
    });
});
