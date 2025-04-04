describe('ActivitySuggestion', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    cy.createDatabaseInfoForActivitySuggestions();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create activity suggestion', () => {
    const NAME = 'Sugestão de atividade';
    const DESCRIPTION = 'Nova sugestão de atividade';
    const REGION = 'Sintra';
    const NUMBER = '5';
    const INSTITUTION_NAME = 'DEMO INSTITUTION';

    // NOTE: the application deadline for a suggested activity
    // must be at least 7 days after the creation date of it
    // calculate dates dynamically
    const creationDate = new Date(); // Current date
    const applicationDeadline = new Date(creationDate);
    applicationDeadline.setDate(creationDate.getDate() + 8);
    const startingDate = new Date(applicationDeadline);
    startingDate.setDate(applicationDeadline.getDate() + 2);
    const endingDate = new Date(startingDate);
    endingDate.setDate(startingDate.getDate() + 2);

    // format dates to ISO strings
    const applicationDeadlineISO = applicationDeadline.toISOString();
    const startingDateISO = startingDate.toISOString();
    const endingDateISO = endingDate.toISOString();

    // login as a demo volunteer
    cy.demoVolunteerLogin()

    // intercept create activity suggestion request and inject date values in the request body
    cy.intercept('POST', '/activitySuggestions//institution/1', (req) => {
      req.body = {
        applicationDeadline: applicationDeadlineISO,
        startingDate: startingDateISO,
        endingDate: endingDateISO
      };
    }).as('register');

    // intercept get institutions
    cy.intercept('GET', '/institutions').as('getInstitutions');

    // go to VolunteerActivitySuggestionView
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.wait('@getInstitutions');


    // - - - - - - - - - - CHECK - - - - - - - - - -
    // verify if the table has 2 activity suggestions before creating a new one
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 2)   // check that the table has 2 rows
      .eq(0)
      .children()
      .should('have.length', 10)  // check that rows have 10 columns


    // go to ActivitySuggestionDialog
    cy.get('[data-cy="newActivitySuggestion"]').click();
    

    // - - - - - - - - - - CREATE - - - - - - - - - -
    // fill dialog form and create activity suggestion
    cy.get('[data-cy="nameInput"]').type(NAME);
    
    cy.get('[data-cy="institutionInput"]').click();
    // select all dropdown items, pick the first one and select it
    cy.get('.v-menu__content .v-list-item').first().click();

    cy.get('[data-cy="descriptionInput"]').type(DESCRIPTION);
    cy.get('[data-cy="regionInput"]').type(REGION);
    cy.get('[data-cy="participantsNumberInput"]').type(NUMBER);

    cy.get('#applicationDeadlineInput-input').click();
    cy.get('#applicationDeadlineInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(1)
      .click({force: true});
    cy.get('#startingDateInput-input').click();
    cy.get('#startingDateInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(9)
      .click({force: true});
    cy.get('#endingDateInput-input').click();
    cy.get('#endingDateInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(11)
      .click({force: true});
    

    // save dialog form - create activity suggestion
    cy.get('[data-cy="saveActivity"]').click()
    // check request was done
    cy.wait('@register')
    

    // - - - - - - - - - - CHECK - - - - - - - - - -
    // check if the table has now 3 activity suggestions
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 3)   // check if the table has now 3 activity suggestions 
      .eq(0)
      .children()
      .should('have.length', 10)  // check that the rows have 10 columns


    // - - - - - - - - - - CHECK - - - - - - - - - -
    // check the values of the columns of the newly created activity suggestion
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(0).should('contain', NAME)
    
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', INSTITUTION_NAME)
    
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(2).should('contain', DESCRIPTION)
    
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', REGION)
    
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(4).should('contain', NUMBER)
    
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(9).should('contain', 'IN_REVIEW')
  });

  it('approve activity suggestion', () => {

    cy.demoMemberLogin();

    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.intercept('GET', '/activitySuggestions/institution/*').as('getActivitySuggestions');
    cy.intercept('PUT', '/activitySuggestions/approve/*/institution/*').as('approveActivitySuggestion');


    // go to InstitutionActivitySuggestionView
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activitysuggestions"]').click();
    cy.wait('@getInstitutions');
    cy.wait('@getActivitySuggestions');

    // - - - - - - - - - - CHECK - - - - - - - - - -
    // check if the first activity suggestion in the table is in the IN_REVIEW state
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(9).should('contain', 'IN_REVIEW')

    
    // approve activity suggestion
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0) // Select the first row
      .within(() => {
        // Click the "Approve" button within the first row
        cy.get('[data-cy="approveActivitySuggestion"]').click();
      });
    cy.wait('@approveActivitySuggestion');

    // before logging out, save the activity suggestion name for the next test
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(0).invoke('text').then((text) => {
        cy.wrap(text).as('activitySuggestionName');
      });

    cy.logout();

    // check on the volunteer side if the activity suggestion was approved

    cy.demoVolunteerLogin();

    cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutionsByVolunteer');
  
    // go to VolunteerActivitySuggestionView
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.wait('@getActivitySuggestions');
    cy.wait('@getInstitutionsByVolunteer');

    // find the activity suggestion by name in the volunteerActivitySuggestionsTable
    cy.get('@activitySuggestionName').then((activitySuggestionName) => {
      cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
        .contains(activitySuggestionName)
        .should('exist');
    });

    // - - - - - - - - - - CHECK - - - - - - - - - -
    // find the activity suggestion by name in the volunteerActivitySuggestionsTable
    // and check if the state is APPROVED
    cy.get('@activitySuggestionName').then((activitySuggestionName) => {
      cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
        .contains(activitySuggestionName)
        .parents('tr')
        .find('td')
        .eq(9)
        .should('contain', 'APPROVED');
    });

  });

  it('reject activity suggestion', () => {

    cy.demoMemberLogin();

    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.intercept('GET', '/activitySuggestions/institution/*').as('getActivitySuggestions');
    cy.intercept('PUT', '/activitySuggestions/reject/*/institution/*').as('rejectActivitySuggestion');


    // go to InstitutionActivitySuggestionView
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activitysuggestions"]').click();
    cy.wait('@getInstitutions');
    cy.wait('@getActivitySuggestions');

    // - - - - - - - - - - CHECK - - - - - - - - - -
    // check if the first activity suggestion in the table is in the IN_REVIEW state
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(9).should('contain', 'IN_REVIEW')

    
    // reject activity suggestion
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0) // Select the first row
      .within(() => {
        // Click the "Reject" button within the first row
        cy.get('[data-cy="rejectActivitySuggestion"]').click();
      });
    cy.wait('@rejectActivitySuggestion');

    // before logging out, save the activity suggestion name for the next test
    cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(0).invoke('text').then((text) => {
        cy.wrap(text).as('activitySuggestionName');
      });

    cy.logout();

    // check on the volunteer side if the activity suggestion was rejected

    cy.demoVolunteerLogin();

    cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutionsByVolunteer');
  
    // go to VolunteerActivitySuggestionView
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.wait('@getActivitySuggestions');
    cy.wait('@getInstitutionsByVolunteer');

    // find the activity suggestion by name in the volunteerActivitySuggestionsTable
    cy.get('@activitySuggestionName').then((activitySuggestionName) => {
      cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
        .contains(activitySuggestionName)
        .should('exist');
    });

    // - - - - - - - - - - CHECK - - - - - - - - - -
    // find the activity suggestion by name in the volunteerActivitySuggestionsTable
    // and check if the state is REJECTED
    cy.get('@activitySuggestionName').then((activitySuggestionName) => {
      cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
        .contains(activitySuggestionName)
        .parents('tr')
        .find('td')
        .eq(9)
        .should('contain', 'REJECTED');
    });

  });

});