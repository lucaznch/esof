describe('ActivitySuggestion', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create activity suggestion', () => {
    const NAME = 'Cãocerto Solidário';
    const DESCRIPTION = 'Concerto solidário';
    const REGION = 'Lisboa';
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

    // go to ActivitySuggestionDialog
    cy.get('[data-cy="newActivitySuggestion"]').click();
    
    // fill form
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
    

    // save form
    cy.get('[data-cy="saveActivity"]').click()
    // check request was done
    cy.wait('@register')
    
    // check results
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 1)   // check that the table has only one row
      .eq(0)
      .children()
      .should('have.length', 10)  // check that the row has 10 columns

    // check the values of the columns
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
});